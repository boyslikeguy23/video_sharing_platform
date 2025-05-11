package org.example.final_project.services;

import org.example.final_project.model.Likes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService {
    Likes doLike(Long userId, Long postId);

    void unLike(Long userId,Long postId);

    List<Likes> getPostLikes(Long postId);

}
