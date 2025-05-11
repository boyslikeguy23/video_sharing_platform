package org.example.final_project.services.impl;


import lombok.AllArgsConstructor;
import org.example.final_project.exception.ApiException;
import org.example.final_project.exception.ResourceNotFoundException;
import org.example.final_project.model.Chat;
import org.example.final_project.model.Message;
import org.example.final_project.model.User;
import org.example.final_project.payloads.SendMessageRequest;
import org.example.final_project.repositories.MessageRepo;
import org.example.final_project.services.ChatService;
import org.example.final_project.services.MessageService;
import org.example.final_project.services.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private ChatService chatService;
    private UserService userService;
    private MessageRepo messageRepo;

    @Override
    public Message saveMessage(SendMessageRequest messageRequest) {
        User user = this.userService.dtoToUser(this.userService.findById(messageRequest.getUserId()));
        Chat chat = this.chatService.findChatByChatId(messageRequest.getChatId());
        Message message = new Message();
        message.setChat(chat);
        message.setUser(user);
        message.setMessageText(messageRequest.getContent());
        message.setInstant(Instant.now());
        return this.messageRepo.save(message);
    }

    @Override
    public List<Message> getChatMessages(Long chatId) {
        return this.messageRepo.findByChatId(chatId).orElseThrow(() -> new ResourceNotFoundException("chat", "id", chatId));
    }

    @Override
    public Message findMessageBYId(Long messageId) {
        return this.messageRepo.findById(messageId).orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
    }

    @Override
    public void deleteMessage(Long messageId, User reqUser) {
        Message message = this.findMessageBYId(messageId);
        if (message.getUser().equals(reqUser)) {
            this.messageRepo.deleteById(messageId);
        }
        throw new ApiException("You Cant Delete Another User's Message" + reqUser.getUsername());
    }
}
