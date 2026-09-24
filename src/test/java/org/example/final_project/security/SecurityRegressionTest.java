package org.example.final_project.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.final_project.configs.AppConfig;
import org.example.final_project.controllers.AuthController;
import org.example.final_project.controllers.ChatController;
import org.example.final_project.controllers.CommentController;
import org.example.final_project.dtos.UserDto;
import org.example.final_project.models.Comments;
import org.example.final_project.models.Message;
import org.example.final_project.models.Post;
import org.example.final_project.models.User;
import org.example.final_project.repositories.*;
import org.example.final_project.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CommentController.class, ChatController.class, AuthController.class})
@Import({AppConfig.class, JwtTokenProvider.class, CommentsServiceImplement.class,
        ChatServiceImplementation.class, UserServiceImplementation.class, UserUserDetailService.class,
        SecurityRegressionTest.Messaging.class})
class SecurityRegressionTest {
    private static final String SECRET = JwtTokenProviderTest.newSecret();

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry properties) {
        properties.add("app.jwt.secret", () -> SECRET);
        properties.add("spring.config.import", () -> "");
    }

    @TestConfiguration
    static class Messaging {
        @Bean
        SimpMessagingTemplate messagingTemplate() {
            return new SimpMessagingTemplate(new ExecutorSubscribableChannel());
        }
    }

    @Autowired MockMvc mvc;
    @Autowired JwtTokenProvider tokens;
    @Autowired PasswordEncoder passwords;
    @MockitoBean UserRepository users;
    @MockitoBean CommentRepository comments;
    @MockitoBean PostRepository posts;
    @MockitoBean StoryRepository stories;
    @MockitoBean MessageRepository messages;
    @MockitoBean PostService postService;

    private User owner;
    private User outsider;
    private Comments comment;
    private Message message;

    @BeforeEach
    void fixtures() throws Exception {
        owner = user(1L, "owner@example.test");
        outsider = user(2L, "outsider@example.test");
        for (User user : List.of(owner, outsider)) {
            when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(users.findById(user.getId())).thenReturn(Optional.of(user));
        }
        UserDto author = new UserDto();
        author.setId(owner.getId());
        comment = new Comments();
        comment.setId(10L);
        comment.setUserDto(author);
        comment.setContent("original");
        when(comments.findById(10L)).thenReturn(Optional.of(comment));

        message = new Message();
        message.setId(20L);
        message.setSender(outsider);
        message.setReceiver(owner);
        when(messages.findById(20L)).thenReturn(Optional.of(message));
    }

    private User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(email);
        user.setName(email);
        return user;
    }

    private String bearer(User user) {
        return "Bearer " + tokens.generateJwtToken(user);
    }

    @Test
    void anonymousMutationIsUnauthorized() throws Exception {
        mvc.perform(delete("/api/comments/delete/10")).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/messages/messages/20/read")).andExpect(status().isUnauthorized());
        verifyNoInteractions(comments, messages);
    }

    @Test
    void malformedHeadersAreUnauthorizedWithoutReachingServices() throws Exception {
        for (String header : List.of("x", "Bearer ", "Bearer invalid-token", "Token abc")) {
            mvc.perform(delete("/api/comments/delete/10").header("Authorization", header))
                    .andExpect(status().isUnauthorized());
        }
        verifyNoInteractions(comments);
    }

    @Test
    void expiredAndWrongSignatureTokensAreUnauthorized() throws Exception {
        String expired = Jwts.builder().setIssuer("video_sharing_platform").claim("username", owner.getEmail())
                .setExpiration(Date.from(Instant.now().minusSeconds(60)))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET))).compact();
        var otherKey = new JwtTokenProvider(JwtTokenProviderTest.newSecret(), java.time.Duration.ofHours(8));
        for (String token : List.of(expired, otherKey.generateJwtToken(owner))) {
            mvc.perform(delete("/api/comments/delete/10").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }
        verifyNoInteractions(comments);
    }

    @Test
    void authorCanEditComment() throws Exception {
        mvc.perform(put("/api/comments/edit").header("Authorization", bearer(owner))
                        .contentType(APPLICATION_JSON).content("{\"id\":10,\"content\":\"updated\"}"))
                .andExpect(status().isAccepted());
        assertEquals("updated", comment.getContent());
        verify(comments).save(comment);
    }

    @Test
    void outsiderCannotEditEvenWhenClaimingToBeAuthorInBody() throws Exception {
        mvc.perform(put("/api/comments/edit").header("Authorization", bearer(outsider))
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":10,\"content\":\"attacker text\",\"userDto\":{\"id\":1}}"))
                .andExpect(status().isForbidden());
        assertEquals("original", comment.getContent());
        verify(comments, never()).save(any());
    }

    @Test
    void authorCanDeleteComment() throws Exception {
        mvc.perform(delete("/api/comments/delete/10").header("Authorization", bearer(owner)))
                .andExpect(status().isAccepted());
        verify(comments).deleteById(10L);
    }

    @Test
    void outsiderCannotDeleteComment() throws Exception {
        mvc.perform(delete("/api/comments/delete/10").header("Authorization", bearer(outsider)))
                .andExpect(status().isForbidden());
        verify(comments, never()).deleteById(any());
    }

    @Test
    void createIgnoresSuppliedIdAuthorAndLikes() throws Exception {
        Post post = new Post();
        post.setId(30L);
        when(postService.findePostById(30L)).thenReturn(post);
        when(comments.save(any())).thenAnswer(call -> call.getArgument(0));
        mvc.perform(post("/api/comments/create/30").header("Authorization", bearer(outsider))
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":10,\"content\":\"new comment\",\"userDto\":{\"id\":1},\"likedByUsers\":[{\"id\":1}]}"))
                .andExpect(status().isCreated());
        ArgumentCaptor<Comments> saved = ArgumentCaptor.forClass(Comments.class);
        verify(comments).save(saved.capture());
        assertNull(saved.getValue().getId());
        assertEquals(2L, saved.getValue().getUserDto().getId());
        assertTrue(saved.getValue().getLikedByUsers().isEmpty());
        assertEquals("original", comment.getContent());
    }

    @Test
    void recipientCanMarkMessageRead() throws Exception {
        mvc.perform(put("/api/messages/messages/20/read").header("Authorization", bearer(owner)))
                .andExpect(status().isOk());
        assertTrue(message.isRead());
        verify(messages).save(message);
    }

    @Test
    void senderAndUnrelatedUserCannotMarkMessageRead() throws Exception {
        User unrelated = user(3L, "third@example.test");
        when(users.findByEmail(unrelated.getEmail())).thenReturn(Optional.of(unrelated));
        for (User user : List.of(outsider, unrelated)) {
            mvc.perform(put("/api/messages/messages/20/read").header("Authorization", bearer(user)))
                    .andExpect(status().isForbidden());
        }
        assertFalse(message.isRead());
        verify(messages, never()).save(any());
    }

    @Test
    void bulkReadOnlyChangesIncomingMessages() throws Exception {
        Message outgoing = new Message();
        outgoing.setId(21L);
        outgoing.setSender(owner);
        outgoing.setReceiver(outsider);
        when(messages.findConversation(1L, 2L)).thenReturn(List.of(message, outgoing));
        mvc.perform(put("/api/messages/read-all/2").header("Authorization", bearer(owner)))
                .andExpect(status().isOk());
        assertTrue(message.isRead());
        assertFalse(outgoing.isRead());
        verify(messages).save(message);
        verify(messages, never()).save(outgoing);
    }

    @Test
    void basicLoginIssuesTokenAcceptedByProtectedEndpoint() throws Exception {
        owner.setPassword(passwords.encode("test-password"));
        var response = mvc.perform(get("/signin").servletPath("/signin")
                        .with(httpBasic(owner.getEmail(), "test-password")))
                .andExpect(status().isAccepted()).andExpect(header().exists("Authorization")).andReturn();
        String token = response.getResponse().getHeader("Authorization");
        assertEquals(owner.getEmail(), tokens.getClaimsFromToken(token).getUsername());
        mvc.perform(put("/api/messages/messages/20/read").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void badLoginDoesNotIssueToken() throws Exception {
        owner.setPassword(passwords.encode("correct-password"));
        mvc.perform(get("/signin").servletPath("/signin").with(httpBasic(owner.getEmail(), "wrong-password")))
                .andExpect(status().isUnauthorized()).andExpect(header().doesNotExist("Authorization"));
    }
}
