package org.example.final_project.services;


import org.example.final_project.model.Chat;
import org.example.final_project.model.User;
import org.example.final_project.payloads.GroupChatRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    Chat createChat(Long userId, User reqUser);

    List<Chat> findAllChatByUserId(Long userId);
    Chat findChatByChatId(Long chatId);

    Chat createGroup(GroupChatRequest request, User reqUser);

    Chat addUserToGroup(Long userId, Long chatId,User reqUser);

    Chat reNameGroup(Long chatId, String groupName,User reqUser);

    Chat removeFromGroup(Long chatId, Long userId,User reqUser);

      void deleteChat(Long chatId, Long UserId);
}
