package org.example.final_project.controllers;

import org.example.final_project.dtos.ChatMessageDeletedEvent;
import org.example.final_project.dtos.ChatMessageRequest;
import org.example.final_project.dtos.ChatMessageResponse;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Message;
import org.example.final_project.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest chatMessage,
                            Principal principal) {
        Long senderId = Long.valueOf(principal.getName());

        try {
            Message savedMessage = chatService.sendMessage(
                    senderId,
                    chatMessage.getReceiverId(),
                    chatMessage.getContent()
            );

            ChatMessageResponse response = ChatMessageResponse.fromMessage(savedMessage);

            // Gửi tin nhắn đến người nhận
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(chatMessage.getReceiverId()),
                    "/queue/messages",
                    response
            );

        } catch (UserException e) {
            // Handle exception
        }
    }


    @MessageMapping("/chat.delete")
    public void deleteMessage(@Payload String messageIdStr, Principal principal) {
        System.out.println("WS DELETE: messageIdStr=" + messageIdStr);
        Long messageId = Long.valueOf(messageIdStr);
        Long userId = Long.valueOf(principal.getName());

        try {
            // Delete the message and get a copy of the deleted message
            Message deletedMessage = chatService.deleteMessageRealtime(messageId, userId);

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

        } catch (UserException e) {
            // Handle exception
        }
    }
}
