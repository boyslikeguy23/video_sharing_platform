package org.example.final_project.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.final_project.dtos.requests.UserRequestDTO;
import org.example.final_project.dtos.responses.UserResponseDTO;
import org.example.final_project.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/nickname")
    public ResponseEntity<List<UserResponseDTO>> searchByNickName(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchByNickName(keyword));
    }

    @GetMapping("/search/fullname")
    public ResponseEntity<List<UserResponseDTO>> searchByFullName(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchByFullName(keyword));
    }
}