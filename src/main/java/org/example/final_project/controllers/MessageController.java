package org.example.final_project.controllers;


import lombok.AllArgsConstructor;
import org.example.final_project.model.Message;
import org.example.final_project.model.User;
import org.example.final_project.payloads.ApiResponse;
import org.example.final_project.payloads.SendMessageRequest;
import org.example.final_project.services.MessageService;
import org.example.final_project.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inst/clone")
@AllArgsConstructor
public class MessageController {

    private MessageService messageService;
    private UserService userService;

    @PostMapping("/save/message")
    public ResponseEntity<Message> saveMessage(@RequestBody SendMessageRequest request, @AuthenticationPrincipal User user ) {
        request.setUserId(user.getId());
        Message message = this.messageService.saveMessage(request);
        return ResponseEntity.ok(message);
    }


    @GetMapping("/chat/{chatId}/messages")
    public ResponseEntity<List<Message>> getChatsMessage(@PathVariable("chatId") Long chatId) {
        List<Message> message = this.messageService.getChatMessages(chatId);
        return ResponseEntity.ok(message);
    }


    @DeleteMapping("/delete/message/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessage(@PathVariable Long messageId, @AuthenticationPrincipal User user) {
        this.messageService.deleteMessage(messageId, user);
        ApiResponse apiResponse = new ApiResponse("Message deleted successfully", true);
        return ResponseEntity.ok(apiResponse);
    }

}
