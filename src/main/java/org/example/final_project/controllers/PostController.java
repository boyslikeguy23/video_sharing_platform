package org.example.final_project.controllers;



import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.final_project.exception.ResourceNotFoundException;
import org.example.final_project.model.Post;
import org.example.final_project.model.User;
import org.example.final_project.payloads.ApiResponse;
import org.example.final_project.payloads.PostRequest;
import org.example.final_project.repositories.UserRepo;
import org.example.final_project.services.PostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/inst/clone")
@AllArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserRepo userRepo;

//    @PostMapping("/upload/post")
//    public ResponseEntity<ApiResponse> uploadPost(@RequestBody PostRequest postRequest, @AuthenticationPrincipal User user) {
//        this.postService.createPost(postRequest, user);
//        return ResponseEntity.ok(new ApiResponse("Post created successfully", true));
//    }
//
//    @PostMapping("/upload/post")
//    public ResponseEntity<ApiResponse> uploadPost(@RequestParam("post") String caption, @RequestParam("image") MultipartFile multipartFile, @AuthenticationPrincipal User user) {
//        this.postService.createPost(caption, multipartFile, user);
//        return ResponseEntity.ok(new ApiResponse("Post created successfully", true));
//    }
@PostMapping("/upload/post")
public ResponseEntity<ApiResponse> uploadPost(
        @RequestPart(value = "postRequest", required = false) PostRequest postRequest,
        @RequestPart(value = "caption", required = false) String caption,
        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
        @AuthenticationPrincipal User user) {

    if (postRequest != null) {
        this.postService.createPost(postRequest, user);
    } else if (caption != null && multipartFile != null) {
        this.postService.createPost(caption, multipartFile, user);
    } else {
        return ResponseEntity.badRequest().body(new ApiResponse("Invalid request", false));
    }

    return ResponseEntity.ok(new ApiResponse("Post created successfully", true));
}

    @DeleteMapping("/delete/post/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        log.info("received a delete request for post id {} from user {}", postId, user);
        if (this.postService.deletePost(postId, user)) {
            return ResponseEntity.ok(new ApiResponse("Deleted successfully", this.postService.deletePost(postId, user)));
        }
        return ResponseEntity.internalServerError().body(new ApiResponse("Unsuccessfully Deleted", false));
    }

    @GetMapping("posts/user/{userId}")
    public ResponseEntity<?> getUserPosts(@PathVariable Long userId) {
        log.info("retrieving posts for user {}", userId);
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));
        List<Post> posts = this.postService.getAllUserPost(user);
        log.info("found {} posts for user {}", posts.size(), user);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getUserPosts(@AuthenticationPrincipal User user) {
        log.info("retrieving posts for user {}", user);
        List<Post> posts = this.postService.getAllUserPost(user);
        log.info("found {} posts for user {}", posts.size(), user);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Post> findPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(this.postService.findById(postId));
    }

    @PostMapping(value = "/upload/post/{postId}")
    public ResponseEntity<Post> uploadImage(@RequestParam("image") MultipartFile multipartFile, @PathVariable Long postId) {
        return ResponseEntity.ok(this.postService.uploadPost(multipartFile, postId));
    }

    @GetMapping(value = "/post/{postId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void servePostImages(HttpServletResponse response, @PathVariable Long postId) {
        this.postService.servePostImages(response, postId);
    }

    @GetMapping(value = "/users/{userId}/posts", produces = MediaType.IMAGE_JPEG_VALUE)
    public void serveUserPostImages(HttpServletResponse response, @PathVariable Long userId) {
        this.postService.serveUserPostImages(response, userId);
    }

    @GetMapping(value = "/current-user/posts", produces = MediaType.IMAGE_JPEG_VALUE)
    public void serveUserPostImages(HttpServletResponse response, @AuthenticationPrincipal User user) {
        this.postService.serveUserPostImages(response, user);
    }


}
