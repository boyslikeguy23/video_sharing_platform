package org.example.final_project.services;

import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Message;
import org.example.final_project.models.User;

import java.util.List;

public interface ChatService {
    Message sendMessage(Long senderId, Long receiverId, String content) throws UserException;

    List<Message> getConversation(Long userId1, Long userId2);

    List<User> getRecentChats(Long userId);

    void markMessageAsRead(Long messageId) throws UserException;

    void deleteMessage(Long messageId, Long userId) throws UserException;

    List<Message> getUnreadMessages(Long userId);

}
