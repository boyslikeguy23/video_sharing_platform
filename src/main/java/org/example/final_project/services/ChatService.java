package org.example.final_project.services;

import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Message;
import org.example.final_project.models.User;

import java.util.List;

public interface ChatService {
    Message sendMessage(Integer senderId, Integer receiverId, String content) throws UserException;
    
    List<Message> getConversation(Integer userId1, Integer userId2);
    
    List<User> getRecentChats(Integer userId);
    
    void markMessageAsRead(Integer messageId) throws UserException;
    
    void deleteMessage(Integer messageId, Integer userId) throws UserException;

    List<Message> getUnreadMessages(Integer userId);

}