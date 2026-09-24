package org.example.final_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.example.final_project.models.Comments;
import org.example.final_project.models.Post;
import org.example.final_project.models.User;
import org.example.final_project.models.Message;
import org.example.final_project.dtos.UserDto;
import org.example.final_project.repositories.PostRepository;
import org.example.final_project.repositories.MessageRepository;
import org.example.final_project.repositories.UserRepository;
import org.example.final_project.dtos.ChatMessageResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FinalProjectApplicationTests {
    private static final String SCHEMA = "security_test_" + UUID.randomUUID().toString().replace("-", "");
    private static final String URL = "jdbc:postgresql://" + env("DB_HOST", "localhost") + ":"
            + env("DB_PORT", "5432") + "/" + env("DB_NAME", "video_sharing_website_db");
    private static final String USERNAME = env("DB_USERNAME", "postgres");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    private static boolean schemaCreated;

    @Autowired TestRestTemplate http;
    @Autowired ObjectMapper objectMapper;
    @Autowired SimpUserRegistry userRegistry;
    @Autowired PostRepository paginationPosts;
    @Autowired MessageRepository paginationMessages;
    @Autowired UserRepository paginationUsers;
    @LocalServerPort int port;

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry properties) throws Exception {
        if (PASSWORD == null) throw new IllegalStateException("DB_PASSWORD is required for PostgreSQL integration tests");
        try (var connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             var statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA " + SCHEMA);
            schemaCreated = true;
        }
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        properties.add("app.jwt.secret", () -> Base64.getEncoder().encodeToString(bytes));
        properties.add("spring.datasource.url", () -> URL);
        properties.add("spring.datasource.username", () -> USERNAME);
        properties.add("spring.datasource.password", () -> PASSWORD);
        properties.add("spring.datasource.hikari.schema", () -> SCHEMA);
        properties.add("spring.jpa.properties.hibernate.default_schema", () -> SCHEMA);
        properties.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        properties.add("spring.config.import", () -> "");
    }

    @AfterAll
    static void removeTestSchema() throws Exception {
        if (schemaCreated && SCHEMA.matches("security_test_[a-f0-9]{32}")) {
            try (var connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 var statement = connection.createStatement()) {
                statement.execute("DROP SCHEMA " + SCHEMA + " CASCADE");
            }
        }
    }

    private record Account(Long id, String token) {}

    private Account signup() {
        String name = "test_" + UUID.randomUUID().toString().replace("-", "");
        String email = name + "@example.test";
        var response = http.postForEntity("/signup",
                Map.of("username", name, "email", email, "name", name, "password", "test-password"), User.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        var signin = http.withBasicAuth(email, "test-password").getForEntity("/signin", String.class);
        assertEquals(HttpStatus.ACCEPTED, signin.getStatusCode());
        String token = signin.getHeaders().getFirst("Authorization");
        assertNotNull(token);
        return new Account(response.getBody().getId(), token);
    }

    private <T> ResponseEntity<T> request(Account account, HttpMethod method, String path, Object body, Class<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(account.token());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return http.exchange(path, method, new HttpEntity<>(body, headers), type);
    }

    @Test
    void authorizationProtectsPersistedCommentsAndReadReceipts() {
        Account author = signup();
        Account other = signup();
        var post = request(author, HttpMethod.POST, "/api/posts/create",
                Map.of("caption", "integration test", "image", "https://example.test/image.png"), Post.class);
        assertEquals(HttpStatus.CREATED, post.getStatusCode());
        var comment = request(author, HttpMethod.POST, "/api/comments/create/" + post.getBody().getId(),
                Map.of("content", "original"), Comments.class);
        assertEquals(HttpStatus.CREATED, comment.getStatusCode());
        Long commentId = comment.getBody().getId();

        assertEquals(HttpStatus.FORBIDDEN, request(other, HttpMethod.PUT, "/api/comments/edit",
                Map.of("id", commentId, "content", "forged"), String.class).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, request(other, HttpMethod.DELETE, "/api/comments/delete/" + commentId,
                null, String.class).getStatusCode());
        var comments = request(author, HttpMethod.GET, "/api/comments/post/" + post.getBody().getId(),
                null, Comments[].class);
        assertEquals("original", comments.getBody()[0].getContent());
        assertEquals(HttpStatus.ACCEPTED, request(author, HttpMethod.PUT, "/api/comments/edit",
                Map.of("id", commentId, "content", "updated"), String.class).getStatusCode());

        var sent = request(author, HttpMethod.POST, "/api/messages/send",
                Map.of("receiverId", other.id(), "content", "hello"), ChatMessageResponse.class);
        assertEquals(HttpStatus.OK, sent.getStatusCode());
        Long messageId = sent.getBody().getId();
        String receiptPath = "/api/messages/messages/" + messageId + "/read";
        assertEquals(HttpStatus.FORBIDDEN, request(author, HttpMethod.PUT, receiptPath, null, String.class).getStatusCode());
        assertEquals(HttpStatus.OK, request(other, HttpMethod.PUT, receiptPath, null, String.class).getStatusCode());
        var unread = request(other, HttpMethod.GET, "/api/messages/unread", null, ChatMessageResponse[].class);
        assertEquals(0, unread.getBody().length);

        assertEquals(HttpStatus.ACCEPTED, request(author, HttpMethod.DELETE,
                "/api/comments/delete/" + commentId, null, String.class).getStatusCode());
        var remaining = request(author, HttpMethod.GET, "/api/comments/post/" + post.getBody().getId(), null, Comments[].class);
        assertEquals(0, remaining.getBody().length);
    }

    @Test
    void accountEndpointsValidateInputAndIgnoreForgedOwnershipFields() throws Exception {
        Account owner = signup();
        Account other = signup();

        var publicProfile = request(owner, HttpMethod.GET, "/api/users/id/" + other.id(), null, String.class);
        assertEquals(HttpStatus.OK, publicProfile.getStatusCode());
        var publicJson = objectMapper.readTree(publicProfile.getBody());
        assertFalse(publicJson.has("email"));
        assertFalse(publicJson.has("mobile"));
        assertFalse(publicJson.has("password"));
        assertFalse(publicJson.has("savedPost"));

        var invalidSignup = http.postForEntity("/signup",
                Map.of("username", "x", "email", "not-an-email", "name", "", "password", "short"), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, invalidSignup.getStatusCode());

        var forgedPost = request(owner, HttpMethod.POST, "/api/posts/create",
                Map.of("id", 999_999L, "caption", "safe", "image", "https://example.test/image.png",
                        "user", Map.of("id", other.id())), Post.class);
        assertEquals(HttpStatus.CREATED, forgedPost.getStatusCode());
        assertNotEquals(999_999L, forgedPost.getBody().getId());
        assertEquals(owner.id(), forgedPost.getBody().getUser().getId());

        var forgedStory = request(owner, HttpMethod.POST, "/api/stories/create",
                Map.of("id", 999_998L, "image", "https://example.test/story.png",
                        "userDto", Map.of("id", other.id())), String.class);
        assertEquals(HttpStatus.OK, forgedStory.getStatusCode());
        var storyJson = objectMapper.readTree(forgedStory.getBody());
        assertNotEquals(999_998L, storyJson.get("id").asLong());
        assertEquals(owner.id(), storyJson.get("userDto").get("id").asLong());

        var changedProfile = request(owner, HttpMethod.PUT, "/api/users/account/edit",
                Map.of("id", other.id(), "name", "Updated name"), String.class);
        assertEquals(HttpStatus.OK, changedProfile.getStatusCode());
        var accountJson = objectMapper.readTree(changedProfile.getBody());
        assertEquals(owner.id(), accountJson.get("id").asLong());
        assertEquals("Updated name", accountJson.get("name").asText());
        assertTrue(accountJson.has("email"));
        assertFalse(accountJson.has("password"));
    }

    @Test
    void realWebSocketPreservesAuthenticatedPrincipalAndDeliversPrivateMessage() throws Exception {
        Account sender = signup();
        Account receiver = signup();
        var client = new WebSocketStompClient(new StandardWebSocketClient());
        var converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        client.setMessageConverter(converter);
        client.start();
        StompSession senderSession = null;
        StompSession receiverSession = null;
        try {
            receiverSession = connect(client, receiver);
            var delivered = new LinkedBlockingQueue<ChatMessageResponse>();
            receiverSession.subscribe("/user/queue/messages", new StompFrameHandler() {
                @Override public Type getPayloadType(StompHeaders headers) { return ChatMessageResponse.class; }
                @Override public void handleFrame(StompHeaders headers, Object payload) {
                    delivered.add((ChatMessageResponse) payload);
                }
            });
            await().atMost(5, TimeUnit.SECONDS).until(() -> {
                var user = userRegistry.getUser(receiver.id().toString());
                return user != null && user.getSessions().stream().flatMap(session -> session.getSubscriptions().stream())
                        .anyMatch(subscription -> "/user/queue/messages".equals(subscription.getDestination()));
            });
            senderSession = connect(client, sender);
            senderSession.send("/app/chat.send", Map.of("receiverId", receiver.id(), "content", "private message"));
            ChatMessageResponse message = delivered.poll(5, TimeUnit.SECONDS);
            assertNotNull(message, "The receiver should receive the private message over its authenticated session");
            assertEquals("private message", message.getContent());
        } finally {
            if (senderSession != null && senderSession.isConnected()) senderSession.disconnect();
            if (receiverSession != null && receiverSession.isConnected()) receiverSession.disconnect();
            client.stop();
        }
    }

    @Test
    void feedUsesStableCursorAcrossTimestampTiesInsertionsAndDeletedBoundary() throws Exception {
        Account author = signup();
        List<Post> posts = seedPosts(author, 45);
        List<Long> expected = posts.stream().sorted(Comparator.comparing(Post::getCreatedAt).reversed()
                .thenComparing(Post::getId, Comparator.reverseOrder())).map(Post::getId).toList();

        // These timestamps are later than other test fixtures, so the global feed starts with this author.
        JsonNode global = page(author, "/api/posts/");
        assertEquals(20, global.get("items").size());
        assertEquals(expected.subList(0, 20), ids(global));
        assertTrue(global.get("hasMore").asBoolean());

        String path = "/api/posts/all/" + author.id();
        JsonNode first = page(author, path);
        List<Long> seen = new ArrayList<>(ids(first));
        String cursor = first.get("nextCursor").asText();
        Long boundaryId = seen.get(seen.size() - 1);

        Post newPost = postFixture(author, LocalDateTime.of(2099, 1, 1, 0, 0));
        paginationPosts.saveAndFlush(newPost);
        paginationPosts.deleteById(boundaryId);
        // The boundary is carried in the cursor, so deleting that row cannot invalidate the next page.
        for (int requests = 0; requests < 5; requests++) {
            JsonNode next = page(author, path + "?cursor=" + cursor);
            seen.addAll(ids(next));
            if (!next.get("hasMore").asBoolean()) {
                assertTrue(next.get("nextCursor").isNull());
                break;
            }
            cursor = next.get("nextCursor").asText();
        }
        assertEquals(expected, seen);
        assertEquals(seen.size(), new HashSet<>(seen).size());
        assertFalse(seen.contains(newPost.getId()));
        assertEquals(newPost.getId(), ids(page(author, path)).get(0));

        // Author filtering remains applied to every cursor page.
        assertEquals(ids(page(author, path + "?size=100")),
                ids(page(author, "/api/posts/following/" + author.id() + "?size=100")));
    }

    @Test
    void postPaginationSupportsEmptyResultsMinimumSizeAndLegacyNullTimestamps() throws Exception {
        Account author = signup();
        String path = "/api/posts/all/" + author.id();
        JsonNode empty = page(author, path);
        assertTrue(empty.get("items").isEmpty());
        assertFalse(empty.get("hasMore").asBoolean());
        assertTrue(empty.get("nextCursor").isNull());

        Post dated = paginationPosts.saveAndFlush(postFixture(author, LocalDateTime.of(2090, 1, 1, 0, 0)));
        Post legacy1 = paginationPosts.saveAndFlush(postFixture(author, null));
        Post legacy2 = paginationPosts.saveAndFlush(postFixture(author, null));
        List<Long> seen = new ArrayList<>();
        String cursor = null;
        for (int i = 0; i < 3; i++) {
            JsonNode result = page(author, path + "?size=1" + (cursor == null ? "" : "&cursor=" + cursor));
            assertEquals(1, result.get("items").size());
            seen.addAll(ids(result));
            if (i < 2) {
                assertTrue(result.get("hasMore").asBoolean());
                cursor = result.get("nextCursor").asText();
            } else {
                assertFalse(result.get("hasMore").asBoolean());
                assertTrue(result.get("nextCursor").isNull());
            }
        }
        assertEquals(List.of(dated.getId(), legacy2.getId(), legacy1.getId()), seen);
    }

    @Test
    void conversationPagesStayPrivateAndBulkReadStillCoversTheWholeConversation() throws Exception {
        Account owner = signup();
        Account peer = signup();
        Account outsider = signup();
        List<Message> messages = seedMessages(owner, peer, 65);
        List<Long> expected = messages.stream().sorted(Comparator.comparing(Message::getSentAt).reversed()
                .thenComparing(Message::getId, Comparator.reverseOrder())).map(Message::getId).toList();
        String path = "/api/messages/conversation/" + peer.id();

        JsonNode first = page(owner, path);
        assertEquals(30, first.get("items").size());
        assertEquals(expected.subList(0, 30), ids(first));
        assertTrue(first.get("hasMore").asBoolean());
        String cursor = first.get("nextCursor").asText();

        JsonNode foreign = page(outsider, path + "?cursor=" + cursor + "&userId1=" + owner.id());
        assertTrue(foreign.get("items").isEmpty());
        assertTrue(foreign.get("nextCursor").isNull());
        assertEquals(HttpStatus.UNAUTHORIZED, http.getForEntity(path + "?cursor=" + cursor, String.class).getStatusCode());

        Message newest = messageFixture(owner, peer, LocalDateTime.of(2099, 1, 1, 0, 0));
        paginationMessages.saveAndFlush(newest);
        paginationMessages.deleteById(ids(first).get(29));
        JsonNode second = page(owner, path + "?cursor=" + cursor);
        assertEquals(expected.subList(30, 60), ids(second));
        JsonNode third = page(owner, path + "?cursor=" + second.get("nextCursor").asText());
        assertEquals(expected.subList(60, 65), ids(third));
        assertFalse(third.get("hasMore").asBoolean());
        assertTrue(third.get("nextCursor").isNull());
        assertEquals(newest.getId(), ids(page(owner, path)).get(0));

        // Both directions share the same conversation, but no other participant pair is visible.
        assertEquals(ids(page(owner, path + "?size=100")),
                ids(page(peer, "/api/messages/conversation/" + owner.id() + "?size=100")));
        seedMessages(outsider, peer, 2);
        JsonNode outsiderPage = page(outsider, path + "?size=100");
        assertEquals(2, outsiderPage.get("items").size());
        assertTrue(ids(outsiderPage).stream().noneMatch(expected::contains));

        assertEquals(HttpStatus.OK, request(peer, HttpMethod.PUT, "/api/messages/read-all/" + owner.id(),
                null, String.class).getStatusCode());
        List<Message> persisted = paginationMessages.findConversation(owner.id(), peer.id());
        assertTrue(persisted.stream().filter(m -> m.getReceiver().getId().equals(peer.id())).allMatch(Message::isRead));
        assertTrue(persisted.stream().filter(m -> m.getReceiver().getId().equals(owner.id())).noneMatch(Message::isRead));
        assertEquals(2, paginationMessages.findConversation(outsider.id(), peer.id()).size());
        assertTrue(paginationMessages.findConversation(outsider.id(), peer.id()).stream().noneMatch(Message::isRead));
    }

    @Test
    void paginationRejectsInvalidSizesAndCursorsAcrossAllListEndpoints() {
        Account account = signup();
        List<String> paths = List.of("/api/posts/", "/api/posts/all/" + account.id(),
                "/api/posts/following/" + account.id(), "/api/messages/conversation/" + account.id());
        String invalidVersion = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("v2|1|2090-01-01T00:00".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        for (String path : paths) {
            for (String query : List.of("size=0", "size=-1", "size=101", "size=2147483647", "size=abc",
                    "cursor=", "cursor=bad!", "cursor=" + invalidVersion, "cursor=" + "a".repeat(161))) {
                assertEquals(HttpStatus.BAD_REQUEST,
                        request(account, HttpMethod.GET, path + "?" + query, null, String.class).getStatusCode(), path + "?" + query);
            }
            assertEquals(HttpStatus.UNAUTHORIZED, http.getForEntity(path, String.class).getStatusCode());
        }
    }

    private JsonNode page(Account account, String path) throws Exception {
        var result = request(account, HttpMethod.GET, path, null, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode(), result.getBody());
        return objectMapper.readTree(result.getBody());
    }

    private List<Long> ids(JsonNode page) {
        List<Long> ids = new ArrayList<>();
        page.get("items").forEach(item -> ids.add(item.get("id").asLong()));
        return ids;
    }

    private Post postFixture(Account author, LocalDateTime time) {
        Post post = new Post();
        UserDto user = new UserDto();
        user.setId(author.id());
        user.setName("Pagination author");
        post.setUser(user);
        post.setImage("https://example.test/pagination.png");
        post.setCaption("Pagination fixture");
        post.setCreatedAt(time);
        return post;
    }

    private List<Post> seedPosts(Account author, int count) {
        return paginationPosts.saveAllAndFlush(IntStream.range(0, count)
                .mapToObj(i -> postFixture(author, LocalDateTime.of(2095, 1, 1, 0, 0).plusSeconds(i / 3))).toList());
    }

    private Message messageFixture(Account sender, Account receiver, LocalDateTime time) {
        Message message = new Message();
        message.setSender(paginationUsers.getReferenceById(sender.id()));
        message.setReceiver(paginationUsers.getReferenceById(receiver.id()));
        message.setContent("Pagination message");
        message.setSentAt(time);
        return message;
    }

    private List<Message> seedMessages(Account first, Account second, int count) {
        return paginationMessages.saveAllAndFlush(IntStream.range(0, count).mapToObj(i ->
                messageFixture(i % 2 == 0 ? first : second, i % 2 == 0 ? second : first,
                        LocalDateTime.of(2095, 1, 1, 0, 0).plusSeconds(i / 3))).toList());
    }

    private StompSession connect(WebSocketStompClient client, Account account) throws Exception {
        StompHeaders headers = new StompHeaders();
        headers.set("Authorization", "Bearer " + account.token());
        return client.connectAsync("ws://localhost:" + port + "/ws/websocket", new WebSocketHttpHeaders(), headers,
                new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);
    }
}
