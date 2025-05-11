package org.example.final_project.services;


import jakarta.servlet.http.HttpServletResponse;
import org.example.final_project.model.User;
import org.example.final_project.payloads.UpdateUserDetails;
import org.example.final_project.payloads.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserService {
    UserDto registerUser(UserDto userDto);

    UserDto updateUser(UpdateUserDetails updateUserDetails, Long userId);

    List<UserDto> findAllUser();

    List<User> searchUsersByUsername(String username);

    UserDto findById(Long userId);

    User dtoToUser(UserDto userDto);
    UserDto userToDto(User user);
    UserDto setProfilePitcher(MultipartFile multipartFile,User user);

   void serveUserProfilePitcher(HttpServletResponse response,User user);

}
