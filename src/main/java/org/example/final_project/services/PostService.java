package org.example.final_project.services;

import jakarta.servlet.http.HttpServletResponse;
import org.example.final_project.model.Post;
import org.example.final_project.model.User;
import org.example.final_project.payloads.PostRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface PostService {
    void createPost(PostRequest postRequest, User user);
    void createPost(String caption,MultipartFile multipartFile, User user);

    Post uploadPost(MultipartFile multipartFile, Long postId);

    boolean deletePost(Long postId, User user);

    List<Post> getAllUserPost(User user);

    Post findById(Long postId);

    List<Post> findByUserId(Long uerId);

    void servePostImages(HttpServletResponse response, Long postId);

    void serveUserPostImages(HttpServletResponse response, Long userId);

    void serveUserPostImages(HttpServletResponse response, User user);


}
