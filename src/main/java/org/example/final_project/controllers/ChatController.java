package org.example.final_project.controllers;

import org.example.final_project.dtos.ChatMessageDeletedEvent;
import org.example.final_project.dtos.ChatMessageRequest;
import org.example.final_project.dtos.ChatMessageResponse;
import org.example.final_project.dtos.RecentChatDto;
import org.example.final_project.dtos.UserDto;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Message;
import org.example.final_project.models.User;
import org.example.final_project.responses.MessageResponse;
import org.example.final_project.services.ChatService;
import org.example.final_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody ChatMessageRequest request) throws UserException {

        User sender = userService.findUserProfile(token);
        Message message = chatService.sendMessage(
                sender.getId(),
                request.getReceiverId(),
                request.getContent()
        );

        return ResponseEntity.ok(ChatMessageResponse.fromMessage(message));
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<ChatMessageResponse>> getConversation(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) throws UserException {

        User currentUser = userService.findUserProfile(token);
        List<Message> messages = chatService.getConversation(currentUser.getId(), userId);

        List<ChatMessageResponse> response = messages.stream()
                .map(ChatMessageResponse::fromMessage)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RecentChatDto>> getRecentChats(
            @RequestHeader("Authorization") String token) throws UserException {

        User currentUser = userService.findUserProfile(token);
        List<RecentChatDto> recentChatsWithLastMessage = chatService.getRecentChatsWithLastMessage(currentUser.getId());

        return ResponseEntity.ok(recentChatsWithLastMessage);
    }

    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long messageId) throws UserException {

        userService.findUserProfile(token); // Validate user
        chatService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable Long messageId) throws UserException {

        User currentUser = userService.findUserProfile(token);

        // Use the real-time deletion method instead of the regular one
        Message deletedMessage = chatService.deleteMessageRealtime(messageId, currentUser.getId());

        // Create deletion event
        ChatMessageDeletedEvent deletionEvent = new ChatMessageDeletedEvent(
                deletedMessage.getId(),
                deletedMessage.getSender().getId(),
                deletedMessage.getReceiver().getId()
        );

        // Send deletion event to the receiver
        messagingTemplate.convertAndSendToUser(
                String.valueOf(deletedMessage.getReceiver().getId()),
                "/queue/messages",
                deletionEvent
        );

        // Also send deletion event to the sender (to update all their clients)
        messagingTemplate.convertAndSendToUser(
                String.valueOf(deletedMessage.getSender().getId()),
                "/queue/messages",
                deletionEvent
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread")
    public ResponseEntity<List<ChatMessageResponse>> getUnreadMessages(
            @RequestHeader("Authorization") String token) throws UserException {

        User currentUser = userService.findUserProfile(token);
        List<Message> unreadMessages = chatService.getUnreadMessages(currentUser.getId());

        List<ChatMessageResponse> response = unreadMessages.stream()
                .map(ChatMessageResponse::fromMessage)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Object>> getUnreadMessageCounts(
            @RequestHeader("Authorization") String token) throws UserException {

        User currentUser = userService.findUserProfile(token);
        List<Message> unreadMessages = chatService.getUnreadMessages(currentUser.getId());

        // Count total unread messages
        int totalUnread = unreadMessages.size();

        // Count unread messages per sender
        Map<Long, Long> unreadCountsBySender = new HashMap<>();
        for (Message message : unreadMessages) {
            Long senderId = message.getSender().getId();
            unreadCountsBySender.put(senderId, unreadCountsBySender.getOrDefault(senderId, (long)0) + 1);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalUnread", totalUnread);
        response.put("unreadCountsBySender", unreadCountsBySender);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/read-all/{userId}")
    public ResponseEntity<MessageResponse> markAllMessagesAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) throws UserException {

        User currentUser = userService.findUserProfile(token);
        List<Message> conversation = chatService.getConversation(currentUser.getId(), userId);

        // Mark all unread messages from the specified user as read
        for (Message message : conversation) {
            if (!message.isRead() && message.getSender().getId().equals(userId) && 
                message.getReceiver().getId().equals(currentUser.getId())) {
                chatService.markMessageAsRead(message.getId());
            }
        }

        return ResponseEntity.ok(new MessageResponse("All messages marked as read"));
    }
}
