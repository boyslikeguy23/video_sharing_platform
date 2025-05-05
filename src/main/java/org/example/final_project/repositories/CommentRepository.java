package org.example.final_project.repositories;

import org.example.final_project.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVideoVideoId(Long videoId);

}
