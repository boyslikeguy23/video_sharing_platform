package org.example.final_project.services;

import org.example.final_project.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FollowService {

    void follow(Long followId, Long followingId);

    List<User> getFollowers(User user);

    List<User> getFollowings(User user);

    void unFollow(Long follower, Long following);

    boolean isFollowing(User follower, User following);

}
