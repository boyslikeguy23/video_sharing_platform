package org.example.final_project.services;

import jakarta.transaction.Transactional;
import org.example.final_project.dtos.RecentChatDto;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Message;
import org.example.final_project.models.User;
import org.example.final_project.repositories.MessageRepository;
import org.example.final_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChatServiceImplementation implements ChatService{
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) throws UserException {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        return messageRepository.save(message);
    }


    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }


//    @Override
//    public List<User> getRecentChats(Long userId) {
//        return messageRepository.findRecentChatUsers(userId);
//    }
    public List<User> getRecentChats(Long userId) {
        Set<User> users = new LinkedHashSet<>();
        users.addAll(messageRepository.findReceiversBySender(userId));
        users.addAll(messageRepository.findSendersByReceiver(userId));
        return new ArrayList<>(users);
    }


    @Override
    public void markMessageAsRead(Long messageId) throws UserException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new UserException("Message not found"));

        message.setRead(true);
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) throws UserException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new UserException("Message not found"));

        // Chỉ người gửi mới có thể xóa tin nhắn
        if (!message.getSender().getId().equals(userId)) {
            throw new UserException("You don't have permission to delete this message");
        }

        messageRepository.delete(message);
    }

    @Override
    public Message deleteMessageRealtime(Long messageId, Long userId) throws UserException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new UserException("Message not found"));

        // Chỉ người gửi mới có thể xóa tin nhắn
        if (!message.getSender().getId().equals(userId)) {
            throw new UserException("You don't have permission to delete this message");
        }

        // Save a copy of the message before deleting it
        Message deletedMessage = new Message();
        deletedMessage.setId(message.getId());
        deletedMessage.setSender(message.getSender());
        deletedMessage.setReceiver(message.getReceiver());
        deletedMessage.setContent(message.getContent());
        deletedMessage.setSentAt(message.getSentAt());
        deletedMessage.setRead(message.isRead());

        // Delete the message
        messageRepository.delete(message);

        return deletedMessage;
    }

    @Override
    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findUnreadMessages(userId);
    }

    @Override
    public List<RecentChatDto> getRecentChatsWithLastMessage(Long userId) {
        List<User> recentUsers = getRecentChats(userId);
        List<RecentChatDto> recentChatsWithLastMessage = new ArrayList<>();

        for (User user : recentUsers) {
            List<Message> messages = messageRepository.findMessagesBetweenUsersOrderedByDate(userId, user.getId());
            Message lastMessage = messages.isEmpty() ? null : messages.get(0);
            if (lastMessage != null) {
                RecentChatDto dto = new RecentChatDto();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setName(user.getName());
                dto.setUserImage(user.getImage());
                dto.setEmail(user.getEmail());
                dto.setLastMessage(lastMessage.getContent());
                dto.setSentAt(lastMessage.getSentAt());
                recentChatsWithLastMessage.add(dto);
            }
        }

        // Sort by sentAt in descending order (most recent first)
        recentChatsWithLastMessage.sort((a, b) -> b.getSentAt().compareTo(a.getSentAt()));

        return recentChatsWithLastMessage;
    }
}
