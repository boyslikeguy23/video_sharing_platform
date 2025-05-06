package org.example.final_project.services.interfaces;

import org.example.final_project.dtos.requests.UserRequestDTO;
import org.example.final_project.dtos.responses.UserResponseDTO;
import java.util.List;

public interface IUserService {
    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO updateUser(Long id, UserRequestDTO dto);

    void deleteUser(Long id);

    UserResponseDTO getUserById(Long id);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> searchByNickName(String keyword);

    List<UserResponseDTO> searchByFullName(String keyword);
}