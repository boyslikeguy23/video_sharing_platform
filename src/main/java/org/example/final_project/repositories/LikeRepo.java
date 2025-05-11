package org.example.final_project.repositories;


import org.example.final_project.model.Likes;
import org.example.final_project.model.Post;
import org.example.final_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepo extends JpaRepository<Likes, Long> {
    Likes findByUserAndPost(User userId, Post postId);

    Optional<List<Likes>> findByPostId(Long postId);
}
