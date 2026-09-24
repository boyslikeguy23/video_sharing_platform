package org.example.final_project.security;

import io.jsonwebtoken.Claims;
import org.example.final_project.repositories.UserRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.Instant;
import java.util.Set;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private static final Set<String> SEND_DESTINATIONS = Set.of("/app/chat.send", "/app/chat.delete");
    private static final Set<String> SUBSCRIPTIONS = Set.of("/user/queue/messages", "/user/queue/errors");
    private final JwtTokenProvider tokens;
    private final UserRepository users;

    public WebSocketAuthInterceptor(JwtTokenProvider tokens, UserRepository users) {
        this.tokens = tokens;
        this.users = users;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new AccessDeniedException("Invalid STOMP message");
        }
        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT.equals(command)) {
            authenticate(accessor);
            return message;
        }
        // Heartbeats and disconnect cleanup carry no application payload.
        if (accessor.getMessageType() == SimpMessageType.HEARTBEAT || command == StompCommand.DISCONNECT) {
            return message;
        }
        if (!(accessor.getUser() instanceof ChatPrincipal principal)
                || !principal.expiresAt().isAfter(Instant.now())) {
            throw new AccessDeniedException("WebSocket authentication required");
        }
        String destination = accessor.getDestination();
        if (command == StompCommand.SEND && destination != null && SEND_DESTINATIONS.contains(destination)) {
            return message;
        }
        if (command == StompCommand.SUBSCRIBE && destination != null && SUBSCRIPTIONS.contains(destination)) {
            return message;
        }
        if (command == StompCommand.UNSUBSCRIBE) {
            return message;
        }
        throw new AccessDeniedException("WebSocket destination is not allowed");
    }

    private void authenticate(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw new AccessDeniedException("WebSocket authentication required");
        }
        Claims claims;
        try {
            claims = tokens.parseToken(authorization.substring(7));
        } catch (RuntimeException ex) {
            // Never include the supplied token or JWT parser exception in logs/errors.
            throw new AccessDeniedException("Invalid or expired access token");
        }
        var user = users.findByEmail(claims.get("username", String.class))
                .orElseThrow(() -> new AccessDeniedException("WebSocket authentication required"));
        accessor.setUser(new ChatPrincipal(user.getId().toString(), claims.getExpiration().toInstant()));
    }

    private record ChatPrincipal(String name, Instant expiresAt) implements Principal {
        @Override
        public String getName() {
            return name;
        }
    }
}
