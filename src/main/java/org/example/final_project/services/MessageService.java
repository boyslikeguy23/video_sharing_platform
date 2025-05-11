package org.example.final_project.services;


import org.example.final_project.model.Message;
import org.example.final_project.model.User;
import org.example.final_project.payloads.SendMessageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {

    Message saveMessage(SendMessageRequest messageRequest);

    List<Message> getChatMessages(Long chatId);

    Message findMessageBYId(Long messageId);

    void deleteMessage(Long messageId, User reqUser);


}
