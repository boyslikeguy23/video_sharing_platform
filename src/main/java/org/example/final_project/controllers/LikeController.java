package org.example.final_project.controllers;



import lombok.AllArgsConstructor;
import org.example.final_project.model.Likes;
import org.example.final_project.services.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inst/clone")
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/doLike/post/{postId}/user/{userId}")
    public ResponseEntity<Likes> likePost(@PathVariable Long postId, @PathVariable Long userId) {

        return ResponseEntity.ok(this.likeService.doLike(userId, postId));
    }

    @GetMapping("/likes/post/{postId}")
    public ResponseEntity<List<Likes>> getPostLikes(@PathVariable Long postId) {
        List<Likes> likes=this.likeService.getPostLikes(postId);
        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/unlike/post/{postId}/user/{userId}")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId,@PathVariable Long userId){
        this.likeService.unLike(userId,postId);
        return ResponseEntity.ok("successFull");
    }





}
