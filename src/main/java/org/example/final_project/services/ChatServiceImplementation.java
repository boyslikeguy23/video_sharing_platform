package org.example.final_project.services;

import jakarta.transaction.Transactional;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Message;
import org.example.final_project.models.User;
import org.example.final_project.repositories.MessageRepository;
import org.example.final_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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



    @Override
    public Message sendMessage(Integer senderId, Integer receiverId, String content) throws UserException {
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
    public List<Message> getConversation(Integer userId1, Integer userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }


//    @Override
//    public List<User> getRecentChats(Integer userId) {
//        return messageRepository.findRecentChatUsers(userId);
//    }
    public List<User> getRecentChats(Integer userId) {
        Set<User> users = new LinkedHashSet<>();
        users.addAll(messageRepository.findReceiversBySender(userId));
        users.addAll(messageRepository.findSendersByReceiver(userId));
        return new ArrayList<>(users);
    }


    @Override
    public void markMessageAsRead(Integer messageId) throws UserException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new UserException("Message not found"));

        message.setRead(true);
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Integer messageId, Integer userId) throws UserException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new UserException("Message not found"));

        // Chỉ người gửi mới có thể xóa tin nhắn
        if (!message.getSender().getId().equals(userId)) {
            throw new UserException("You don't have permission to delete this message");
        }

        messageRepository.delete(message);
    }

    @Override
    public List<Message> getUnreadMessages(Integer userId) {
        return messageRepository.findUnreadMessages(userId);
    }


}
