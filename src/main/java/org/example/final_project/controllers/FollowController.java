package org.example.final_project.controllers;



import lombok.AllArgsConstructor;
import org.example.final_project.exception.ResourceNotFoundException;
import org.example.final_project.model.User;
import org.example.final_project.payloads.ApiResponse;
import org.example.final_project.repositories.UserRepo;
import org.example.final_project.services.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inst/clone")
@AllArgsConstructor
public class FollowController {

    private FollowService followService;
    private UserRepo userRepo;


    @PostMapping("/follow/followerId/{followerId}")
    public ResponseEntity<ApiResponse> follow(@PathVariable Long followerId, @AuthenticationPrincipal User user) {
        this.followService.follow(user.getId(),followerId);
        return ResponseEntity.ok(new ApiResponse("Followed successfully", true));
    }

    @GetMapping("/followers/user/{userId}")
    public ResponseEntity<List<User>> getFollowers(@PathVariable Long userId) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));
        System.out.println(this.followService.getFollowers(user));
        return ResponseEntity.ok(this.followService.getFollowers(user));
    }

   @GetMapping("/followings/user/{userId}")
    public ResponseEntity<List<User>> getFollowings(@PathVariable Long userId) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));
       System.out.println(this.followService.getFollowings(user));
        return ResponseEntity.ok(this.followService.getFollowings(user));
    }

    @DeleteMapping("/unfollow/followerId/{followerId}")
    public ResponseEntity<ApiResponse> unFollow(@PathVariable Long followerId, @AuthenticationPrincipal User user) {
        this.followService.unFollow(followerId, user.getId());
        return ResponseEntity.ok(new ApiResponse("Unfollowed successfully", true));
    }

    @GetMapping("/isFollowing/followerId/{followerId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long followerId, @AuthenticationPrincipal User user) {
        User follower = this.userRepo.findById(followerId).orElseThrow(() -> new ResourceNotFoundException("user", "id", followerId));
        return ResponseEntity.ok(this.followService.isFollowing(follower, user));
    }
}
