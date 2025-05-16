package org.example.final_project.repositories;

import org.example.final_project.models.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comments, Integer> {
	

    @Query("SELECT c FROM Comments c WHERE c.post.id = :postId")
    List<Comments> findCommentsByPostId(@Param("postId") Integer postId);

}
