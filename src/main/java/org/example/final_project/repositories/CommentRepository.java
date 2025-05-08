package org.example.final_project.repositories;

import org.example.final_project.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVideo_VideoId(Long videoId);
    List<Comment> findByUser_Id(Long userId);
}
