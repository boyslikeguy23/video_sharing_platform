package org.example.final_project.mappers;

import org.example.final_project.dtos.requests.UserRequestDTO;
import org.example.final_project.dtos.responses.UserResponseDTO;
import org.example.final_project.entities.Role;
import org.example.final_project.entities.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto, Role role) {
        return User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword()) // Bạn nên mã hoá ở Service!
                .fullName(dto.getFullName())
                .nickName(dto.getNickName())
                .phone(dto.getPhone())
                .dateOfBirth(dto.getDateOfBirth())
                .avatarUrl(dto.getAvatarUrl())
                .isActive(dto.isActive())
                .role(role)
                .build();
    }

    public static UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .nickName(user.getNickName())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.isActive())
                .roleName(user.getRole().getName())
                .build();
    }
}
