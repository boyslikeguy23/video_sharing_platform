package org.example.final_project.services.impl;



import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.final_project.exception.ApiException;
import org.example.final_project.exception.ResourceNotFoundException;
import org.example.final_project.model.Follow;
import org.example.final_project.model.User;
import org.example.final_project.repositories.FollowRepo;
import org.example.final_project.repositories.UserRepo;
import org.example.final_project.services.FollowService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class FollowServiceImpl implements FollowService {
    private final FollowRepo followRepo;
    private final UserRepo userRepo;
    
    @Override
    public void follow(Long followId, Long followingId) {
        User follower = this.userRepo.findById(followId).orElseThrow(() -> new ResourceNotFoundException("user", "id", followId));
        User following = this.userRepo.findById(followingId).orElseThrow(() -> new ResourceNotFoundException("user", "id", followId));

        if (!this.isFollowing(follower, following)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            this.followRepo.save(follow);
        }
    }
    @Override
    public List<User> getFollowers(User user) {
        List<Follow> follows = this.followRepo.findByFollowing(user);
        return follows
                .stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFollowings(User user) {
        List<Follow> follows = this.followRepo.findByFollower(user);
        System.out.println(follows);
        return follows
                .stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());
    }

    @Override
    public void unFollow(Long followerId, Long followingId) {
        User follower = this.userRepo.findById(followerId).orElseThrow(() -> new ResourceNotFoundException("user", "id", followerId));
        User following = this.userRepo.findById(followingId).orElseThrow(() -> new ResourceNotFoundException("user", "id", followingId));
        Follow follow = this.followRepo.findByFollowerAndFollowing(following,follower).orElseThrow(() -> new ApiException("Your Not Following this User "));
        System.out.println(follow);
        this.followRepo.delete(follow);
    }
    @Override
    public boolean isFollowing(User followerId, User followingId) {
        return this.followRepo.existsByFollowerAndFollowing(followingId,followerId);
    }
}
