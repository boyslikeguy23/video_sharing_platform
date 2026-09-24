package org.example.final_project.security;

import org.example.final_project.models.User;
import org.example.final_project.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebSocketAuthInterceptorTest {
    private final JwtTokenProvider tokens = new JwtTokenProvider(JwtTokenProviderTest.newSecret(), Duration.ofHours(8));
    private final UserRepository users = mock(UserRepository.class);
    private final WebSocketAuthInterceptor security = new WebSocketAuthInterceptor(tokens, users);
    private User user;

    @BeforeEach
    void fixture() {
        user = new User();
        user.setId(7L);
        user.setEmail("chat@example.test");
        when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    private Message<byte[]> frame(StompHeaderAccessor accessor) {
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private Principal connect() {
        var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer " + tokens.generateJwtToken(user));
        Message<byte[]> message = frame(accessor);
        assertSame(message, security.preSend(message, null));
        assertEquals("7", accessor.getUser().getName());
        return accessor.getUser();
    }

    @Test
    void validConnectUsesServerResolvedUserId() {
        connect();
    }

    @Test
    void missingMalformedAndInvalidCredentialsAreDenied() {
        for (String header : List.of("", "Bearer ", "Bearer invalid", "Basic abc")) {
            var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
            if (!header.isEmpty()) accessor.setNativeHeader("Authorization", header);
            assertThrows(AccessDeniedException.class, () -> security.preSend(frame(accessor), null));
        }
    }

    @Test
    void deletedUserCannotConnect() {
        when(users.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer " + tokens.generateJwtToken(user));
        assertThrows(AccessDeniedException.class, () -> security.preSend(frame(accessor), null));
    }

    @Test
    void authenticatedClientCanUseApplicationChatAndOwnSubscription() {
        Principal principal = connect();
        for (String destination : List.of("/app/chat.send", "/app/chat.delete")) {
            var accessor = StompHeaderAccessor.create(StompCommand.SEND);
            accessor.setUser(principal);
            accessor.setDestination(destination);
            Message<byte[]> message = frame(accessor);
            assertSame(message, security.preSend(message, null));
        }
        var accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setUser(principal);
        accessor.setDestination("/user/queue/messages");
        Message<byte[]> message = frame(accessor);
        assertSame(message, security.preSend(message, null));
    }

    @Test
    void anonymousAndUnverifiedPrincipalsCannotSendOrSubscribe() {
        for (StompCommand command : List.of(StompCommand.SEND, StompCommand.SUBSCRIBE)) {
            var accessor = StompHeaderAccessor.create(command);
            accessor.setDestination("/app/chat.send");
            Message<byte[]> anonymous = frame(accessor);
            assertThrows(AccessDeniedException.class, () -> security.preSend(anonymous, null));
            accessor = StompHeaderAccessor.create(command);
            accessor.setUser(() -> "7");
            accessor.setDestination("/user/queue/messages");
            Message<byte[]> message = frame(accessor);
            assertThrows(AccessDeniedException.class, () -> security.preSend(message, null));
        }
    }

    @Test
    void directBrokerMessagesAndForeignSubscriptionsAreDenied() {
        Principal principal = connect();
        for (StompCommand command : List.of(StompCommand.SEND, StompCommand.SUBSCRIBE)) {
            for (String destination : List.of("/queue/messages", "/queue/**", "/topic/test",
                    "/user/8/queue/messages", "/user/queue/messages-other-session", "/app/unknown")) {
                var accessor = StompHeaderAccessor.create(command);
                accessor.setUser(principal);
                accessor.setDestination(destination);
                assertThrows(AccessDeniedException.class, () -> security.preSend(frame(accessor), null));
            }
        }
    }
}
