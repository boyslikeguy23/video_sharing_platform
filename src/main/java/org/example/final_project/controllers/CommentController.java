package org.example.final_project.controllers;


import org.example.final_project.exceptions.CommentException;
import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Comments;
import org.example.final_project.models.User;
import org.example.final_project.responses.MessageResponse;
import org.example.final_project.services.CommentService;
import org.example.final_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/create/{postId}")
	public ResponseEntity<Comments> createCommentHandler(@RequestBody Comments comment, @PathVariable("postId") Integer postId, @RequestHeader("Authorization")String token) throws PostException, UserException {
		User user = userService.findUserProfile(token);
		Comments createdComment = commentService.createComment(comment, postId, user.getId());
		System.out.println("created comment c--- "+createdComment.getContent());
		return new ResponseEntity<Comments>(createdComment,HttpStatus.CREATED);
	}
	@PutMapping("/like/{commentId}")
	public ResponseEntity<Comments> likeCommentHandler(@PathVariable Integer commentId, @RequestHeader("Authorization")String token) throws UserException, CommentException {
		System.out.println("----------- like comment id ---------- ");
		User user = userService.findUserProfile(token);
		Comments likedComment=commentService.likeComment(commentId, user.getId());
		System.out.println("liked comment - : "+likedComment);
		return new ResponseEntity<Comments>(likedComment,HttpStatus.OK);
	}
	
	
	@PutMapping("/unlike/{commentId}")
	public ResponseEntity<Comments> unlikeCommentHandler(@RequestHeader("Authorization")String token, @PathVariable Integer commentId) throws UserException, CommentException{
		User user = userService.findUserProfile(token);
		Comments likedComment=commentService.unlikeComment(commentId, user.getId());
		
		return new ResponseEntity<Comments>(likedComment,HttpStatus.OK);
	}
	
	@PutMapping("/edit")
	public ResponseEntity<MessageResponse> editCommentHandler(@RequestBody Comments comment) throws CommentException{
		
		commentService.editComment(comment, comment.getId());
		
		MessageResponse res=new MessageResponse("Comment Updated Successfully");
		
		return new ResponseEntity<MessageResponse>(res,HttpStatus.ACCEPTED);
	}
	

	@DeleteMapping("/delete/{commentId}")
	public ResponseEntity<MessageResponse> deleteCommentHandler(@PathVariable Integer commentId) throws CommentException{
		
		commentService.deleteCommentById(commentId);
		
		MessageResponse res=new MessageResponse("Comment Delete Successfully");
		
		return new ResponseEntity<MessageResponse>(res,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/post/{postId}")
	public ResponseEntity<List<Comments>> getCommentHandler(@PathVariable Integer postId) throws CommentException, PostException{
		
		List<Comments> comments=commentService.findCommentByPostId(postId);
		
		MessageResponse res=new MessageResponse("Comment Updated Successfully");
		
		return new ResponseEntity<>(comments,HttpStatus.ACCEPTED);
	}
}
//