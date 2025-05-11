package org.example.final_project.services.impl;



import lombok.AllArgsConstructor;
import org.example.final_project.exception.ResourceNotFoundException;
import org.example.final_project.model.Likes;
import org.example.final_project.model.Post;
import org.example.final_project.model.User;
import org.example.final_project.repositories.LikeRepo;
import org.example.final_project.repositories.UserRepo;
import org.example.final_project.services.LikeService;
import org.example.final_project.services.PostService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepo likeRepo;
    private final UserRepo userRepo;
    private final PostService postService;

    @Override
    public Likes doLike(Long userId, Long postId) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));
        Post post = this.postService.findById(postId);

        Likes like = this.likeRepo.findByUserAndPost(user, post);
        System.out.println(like);
        if (like != null) {
            return like;
        }
        Likes likes = new Likes();
        likes.setUser(user);
        likes.setPost(post);
        likes.setCreateAt(Instant.now());
        System.out.println(likes);
        return this.likeRepo.save(likes);
    }

    @Override
    public void unLike(Long userId, Long postId) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));
        Post post = this.postService.findById(postId);
        Likes like = this.likeRepo.findByUserAndPost(user, post);
        if (like != null) {
            this.likeRepo.delete(like);
        }
    }

    @Override
    public List<Likes> getPostLikes(Long postId) {
        return this.likeRepo.findByPostId(postId).orElseThrow(() -> new ResourceNotFoundException("post", "id", postId));
    }
}
