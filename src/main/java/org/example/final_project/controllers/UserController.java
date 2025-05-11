package org.example.final_project.controllers;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.final_project.exception.ApiException;
import org.example.final_project.model.User;
import org.example.final_project.payloads.UpdateUserDetails;
import org.example.final_project.payloads.UserDto;
import org.example.final_project.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/inst/clone")
public class UserController {

    private final UserService userService;

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UpdateUserDetails updateUserDetalis, @AuthenticationPrincipal User user) {
        UserDto users;
        try {
            users = this.userService.updateUser(updateUserDetalis, user.getId());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(this.userService.findAllUser());
    }

    @GetMapping("/current-user")
    @Transactional
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto> findById(@PathVariable Long userId) {
        return ResponseEntity.ok(this.userService.findById(userId));
    }

    @PostMapping("/setProfilePitcher")
    public ResponseEntity<UserDto> setProfilePitcher(@RequestParam("image") MultipartFile multipartFile, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(this.userService.setProfilePitcher(multipartFile, user));
    }

    @GetMapping(value = "/user/profilePitcher", produces = MediaType.IMAGE_JPEG_VALUE)
    public void serveUserProfilePitcher(HttpServletResponse response, @AuthenticationPrincipal User user) {
        this.userService.serveUserProfilePitcher(response, user);
    }

    @GetMapping("/search/keyword/{keyword}")
    public ResponseEntity<List<User>> searchUserWithKeyword(@PathVariable String keyword) {
        List<User> users = this.userService.searchUsersByUsername(keyword);
        return ResponseEntity.ok(users);
    }
}
