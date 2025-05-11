package org.example.final_project.services.impl;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.final_project.exception.ApiException;
import org.example.final_project.exception.ResourceNotFoundException;
import org.example.final_project.model.Chat;
import org.example.final_project.model.User;
import org.example.final_project.payloads.GroupChatRequest;
import org.example.final_project.repositories.ChatRepo;
import org.example.final_project.repositories.UserRepo;
import org.example.final_project.services.ChatService;
import org.example.final_project.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {
    private ChatRepo chatRepo;
    private UserRepo userRepo;
    private UserService userService;
    private ModelMapper modelMapper;


    @Override
    public Chat createChat(Long userId, User reqUser) {
        System.out.println(reqUser.toString());
        User user = this.userService.dtoToUser(this.userService.findById(userId));
        Chat isChatExist = this.chatRepo.findSingleChatByUserId(user, reqUser);
        if (isChatExist != null) {
            return isChatExist;
        }
        Chat chat = new Chat();
        chat.setCreateBy(reqUser);
        chat.getUsers().add(user);
        chat.getUsers().add(reqUser);
        chat.setGroup(false);
        System.out.println(chat);
        return this.chatRepo.save(chat);
    }

    @Override
    public List<Chat> findAllChatByUserId(Long userId) {
        User user = this.modelMapper.map(this.userService.findById(userId), User.class);
        return this.chatRepo.findByUsersId(user.getId());
    }

    @Override
    public Chat findChatByChatId(Long chatId) {
        return this.chatRepo.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("Chat", "id", chatId));
    }

    @Override
    public Chat createGroup(GroupChatRequest request, User reqUser) {
        Chat group = new Chat();
        group.setGroup(true);
        group.setChatImage(request.getChatImage());
        group.setChatName(request.getChatName());
        group.setCreateBy(reqUser);
        group.getAdmins().add(reqUser);
        request.getUserIDs().stream().map(userId -> userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "id", userId))).forEach(user -> group.getUsers().add(user));
        return this.chatRepo.save(group);
    }

    @Override
    public Chat addUserToGroup(Long userId, Long chatId, User reqUser) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));
        Chat group = this.chatRepo.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("Chat", "id", chatId));
        if (group.getAdmins().contains(reqUser)) {
            group.getUsers().add(user);
            return this.chatRepo.save(group);
        }
        throw new ApiException("You don't have any access to add user in the group");
    }

    @Override
    public Chat reNameGroup(Long chatId, String groupName, User reqUser) {
        Chat group = this.chatRepo.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("Chat", "id", chatId));
        if (group.getAdmins().contains(reqUser)) {
            group.setChatName(groupName);
            return this.chatRepo.save(group);
        }
        throw new ApiException("You don't have access to reName Group");
    }

    @Override
    public Chat removeFromGroup(Long chatId, Long userId, User reqUser) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));
        Chat group = this.chatRepo.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("Chat", "id", chatId));
        if (group.getAdmins().contains(reqUser)) {
            group.getUsers().remove(user);
            return this.chatRepo.save(group);
        }
        throw new ApiException("You don't have access to remove user");

    }

    @Override
    public void deleteChat(Long chatId, Long UserId) {
        Chat chat = this.chatRepo.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("Chat", "id", chatId));
        this.chatRepo.deleteById(chat.getId());
    }
}
