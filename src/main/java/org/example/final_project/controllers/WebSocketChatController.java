package org.example.final_project.controllers;

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
        Integer senderId = Integer.valueOf(principal.getName());

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

}
