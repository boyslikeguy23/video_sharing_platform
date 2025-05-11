package org.example.final_project.controllers;



import lombok.AllArgsConstructor;
import org.example.final_project.model.Chat;
import org.example.final_project.model.User;
import org.example.final_project.payloads.ApiResponse;
import org.example.final_project.services.ChatService;
import org.example.final_project.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inst/clone")
@AllArgsConstructor

public class ChatController {


    private UserService userService;
    private ChatService chatService;


    @PostMapping("/single/chat/user/{userId}")
    private ResponseEntity<Chat> createChat(@PathVariable("userId") Long userId, @AuthenticationPrincipal User requestUser) {
        User user = this.userService.dtoToUser(this.userService.findById(userId));
        Chat chat = this.chatService.createChat(user.getId(), requestUser);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<Chat> findChatByChatId(@PathVariable("chatId") Long chatId) {
        return ResponseEntity.ok(this.chatService.findChatByChatId(chatId));
    }

    @GetMapping("/users/chat")
    public ResponseEntity<List<Chat>> findAllUserChat(@AuthenticationPrincipal User user) {
        List<Chat> chats = this.chatService.findAllChatByUserId(user.getId());
        return ResponseEntity.ok(chats);
    }

    @PutMapping("/chat/{chatId}/add/user/{userId}")
    public ResponseEntity<Chat> addUsersToGroup(@PathVariable Long chatId, @PathVariable Long userId, @AuthenticationPrincipal User user) {
        Chat chat = this.chatService.addUserToGroup(userId, chatId, user);
        return ResponseEntity.ok(chat);
    }

    @PutMapping("/chat/{chatId}/remove/user/{userId}")
    public ResponseEntity<Chat> removeUsersToGroup(@PathVariable Long chatId, @PathVariable Long userId, @AuthenticationPrincipal User user) {
        Chat chat = this.chatService.removeFromGroup(chatId, userId, user);
        return ResponseEntity.ok(chat);
    }

    @DeleteMapping("/delete/chat/{chatId}")
    public ResponseEntity<ApiResponse> deleteChat(@PathVariable Long chatId, @AuthenticationPrincipal User reqUser) {
        this.chatService.deleteChat(chatId, reqUser.getId());
        ApiResponse response = new ApiResponse("Chat is deleted successfully", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
