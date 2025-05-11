package org.example.final_project.services;


import org.example.final_project.model.Comment;
import org.example.final_project.payloads.CommentRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    Comment addComment(CommentRequest commentRequest, Long postId, Long userId);
    List<Comment> getCommentsByPostId(Long postId);

    void deleteComment(Long commentId, Long userId);

    Comment getCommentById(Long commentId);

}
