package org.example.final_project.controllers;



import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.final_project.model.Comment;
import org.example.final_project.payloads.CommentRequest;
import org.example.final_project.services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inst/clone")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add/comment/post/{postId}/user/{userId}")
    public ResponseEntity<Comment> addComment(@Valid @RequestBody CommentRequest commentRequest, @PathVariable Long postId, @PathVariable Long userId) {
        return ResponseEntity.ok(this.commentService.addComment(commentRequest, postId, userId));
    }

    @GetMapping("/get/comment/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(this.commentService.getCommentsByPostId(postId));
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        return ResponseEntity.ok(this.commentService.getCommentById(commentId));
    }

    @DeleteMapping("/delete/comment/{commentId}/user/{userId}")
    public ResponseEntity<?>deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        this.commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("successFull");

    }


}
