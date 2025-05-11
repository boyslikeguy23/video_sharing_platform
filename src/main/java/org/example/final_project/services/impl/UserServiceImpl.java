package org.example.final_project.services.impl;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.final_project.configs.AppConstants;
import org.example.final_project.exception.EmailAlreadyExistsException;
import org.example.final_project.exception.ResourceNotFoundException;
import org.example.final_project.exception.UsernameAlreadyExistsException;
import org.example.final_project.model.Role;
import org.example.final_project.model.User;
import org.example.final_project.payloads.UpdateUserDetails;
import org.example.final_project.payloads.UserDto;
import org.example.final_project.repositories.RoleRepo;
import org.example.final_project.repositories.UserRepo;
import org.example.final_project.services.FileService;
import org.example.final_project.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;
    private RoleRepo roleRepo;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private FileService fileService;


    @Override
    public UserDto registerUser(UserDto user) {
        log.info("registering user {}", user.getUsername());
        if (userRepo.existsByUsername(user.getUsername())) {
            log.warn("username {} already exists.", user.getUsername());
            throw new UsernameAlreadyExistsException(
                    String.format("username %s already exists", user.getUsername()));
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            log.warn("email {} already exists.", user.getEmail());
            throw new EmailAlreadyExistsException(
                    String.format("email %s already exists", user.getEmail()));
        }
        user.setActive(true);
        user.setCreatedAt(Instant.now());
        Role role = this.roleRepo.findById(AppConstants.APP_USER).get();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(role);
        User user1 = this.dtoToUser(user);
        return this.userToDto(this.userRepo.save(user1));
    }

    @Override
    public UserDto updateUser(UpdateUserDetails updateUserDetails, Long userId) {
        if (this.userRepo.existsByUsername(updateUserDetails.getUserName())) {
            throw new UsernameAlreadyExistsException(
                    String.format("userName %s already exists", updateUserDetails.getUserName())
            );
        }
        User user = this.userRepo.findById(userId).orElseThrow();
        user.setUsername(updateUserDetails.getUserName());
        user.setName(updateUserDetails.getName());
        user.setBio(updateUserDetails.getBio());
        user.setUpdatedAt(Instant.now());
        return this.userToDto(this.userRepo.save(user));
    }


    @Override
    public List<UserDto> findAllUser() {
        log.info("retrieving all user ");
        return Collections.singletonList(this.modelMapper.map(this.userRepo.findAll(), UserDto.class));
    }

    @Override
    public List<User> searchUsersByUsername(String username) {
        return this.userRepo.findByUsernameOrUsernameStartsWith(username, username);
    }

    @Override
    public UserDto findById(Long userId) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));
        log.info("user {}", user);
        return this.userToDto(user);
    }

    @Override
    public UserDto setProfilePitcher(MultipartFile multipartFile, User user) {
        String oldProfilePitcherName = user.getUserProfileImage();
        if (oldProfilePitcherName != null) {
            this.fileService.deleteProfilePicture(oldProfilePitcherName, "image/user/");
        }
        String image = this.fileService.uploadImage("image/user/", multipartFile);
        user.setUserProfileImage(image);
        this.userRepo.save(user);
        return this.userToDto(user);
    }

    @Override
    public void serveUserProfilePitcher(HttpServletResponse response, User user) {
        try {
            InputStream resource = this.fileService.getResource("image/user/", user.getUserProfileImage());
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            StreamUtils.copy(resource, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User dtoToUser(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

    public UserDto userToDto(User user) {
        return this.modelMapper.map(user, UserDto.class);
    }


}
