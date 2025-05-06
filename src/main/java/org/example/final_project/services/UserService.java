package org.example.final_project.services;

import lombok.RequiredArgsConstructor;
import org.example.final_project.dtos.requests.UserRequestDTO;
import org.example.final_project.dtos.responses.UserResponseDTO;
import org.example.final_project.entities.Role;
import org.example.final_project.entities.User;
import org.example.final_project.exceptions.ResourceNotFoundException;
import org.example.final_project.mappers.UserMapper;
import org.example.final_project.repositories.RoleRepository;
import org.example.final_project.repositories.UserRepository;
import org.example.final_project.services.interfaces.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User user = UserMapper.toEntity(dto, role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return UserMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setNickName(dto.getNickName());
        user.setPhone(dto.getPhone());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setActive(dto.isActive());
        user.setRole(role);

        return UserMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> searchByNickName(String keyword) {
        return userRepository.findByNickNameContainingIgnoreCase(keyword)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> searchByFullName(String keyword) {
        return userRepository.findByFullNameContainingIgnoreCase(keyword)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}