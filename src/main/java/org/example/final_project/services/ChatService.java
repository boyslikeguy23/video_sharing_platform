package org.example.final_project.services;

import org.example.final_project.dtos.RecentChatDto;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Message;
import org.example.final_project.models.User;

import java.util.List;

public interface ChatService {
    Message sendMessage(Long senderId, Long receiverId, String content) throws UserException;

    List<Message> getConversation(Long userId1, Long userId2);

    List<User> getRecentChats(Long userId);

    List<RecentChatDto> getRecentChatsWithLastMessage(Long userId);

    void markMessageAsRead(Long messageId) throws UserException;

    void deleteMessage(Long messageId, Long userId) throws UserException;

    /**
     * Delete a message and broadcast the deletion event to relevant users
     * @param messageId ID of the message to delete
     * @param userId ID of the user requesting deletion
     * @return The deleted message
     * @throws UserException if message not found or user doesn't have permission
     */
    Message deleteMessageRealtime(Long messageId, Long userId) throws UserException;

    List<Message> getUnreadMessages(Long userId);

}
