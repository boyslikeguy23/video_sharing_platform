package org.example.final_project.controllers;


import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.dtos.CreatePostRequest;
import org.example.final_project.dtos.CursorPage;
import org.example.final_project.models.Post;
import org.example.final_project.models.User;
import org.example.final_project.responses.MessageResponse;
import org.example.final_project.services.PostService;
import org.example.final_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<Post> createPostHandler(@Valid @RequestBody CreatePostRequest request, @RequestHeader("Authorization") String token) throws UserException {
		User user=userService.findUserProfile(token);
		Post createdPost = postService.createPost(request, user.getId());

		return new ResponseEntity<Post>(createdPost, HttpStatus.CREATED);
	}



	@GetMapping("/all/{userId}")
	public ResponseEntity<CursorPage<Post>> findPostByUserIdHandler(@PathVariable("userId") Long userId,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String cursor) {
		return ResponseEntity.ok(postService.findPostByUserId(userId, size, cursor));
	}



	@GetMapping("/following/{userIds}")
	public ResponseEntity<CursorPage<Post>> findAllPostByUserIds(@PathVariable("userIds") List<Long> userIds,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String cursor) {
		return ResponseEntity.ok(postService.findAllPostByUserIds(userIds, size, cursor));
	}


	@GetMapping({"", "/"})
	public ResponseEntity<CursorPage<Post>> findAllPostHandler(@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) String cursor) {
		return ResponseEntity.ok(postService.findAllPost(size, cursor));
	}

	@GetMapping("/{postId}")
	public ResponseEntity<Post> findPostByIdHandler(@PathVariable Long postId) throws PostException{
		Post post=postService.findePostById(postId);

		return new ResponseEntity<Post>(post,HttpStatus.OK);
	}


	@PutMapping("/like/{postId}")
	public ResponseEntity<Post> likePostHandler(@PathVariable("postId") Long postId, @RequestHeader("Authorization") String token) throws UserException, PostException{

		User user=userService.findUserProfile(token);

		Post updatedPost=postService.likePost(postId, user.getId());

		return new ResponseEntity<Post>(updatedPost,HttpStatus.OK);

	}


	@PutMapping("/unlike/{postId}")
	public ResponseEntity<Post> unLikePostHandler(@PathVariable("postId") Long postId, @RequestHeader("Authorization") String token) throws UserException, PostException{

		User reqUser=userService.findUserProfile(token);

		Post updatedPost=postService.unLikePost(postId, reqUser.getId());

		return new ResponseEntity<Post>(updatedPost,HttpStatus.OK);

	}


	@DeleteMapping("/delete/{postId}")
	public ResponseEntity<MessageResponse> deletePostHandler(@PathVariable Long postId, @RequestHeader("Authorization") String token) throws UserException, PostException{

		User reqUser=userService.findUserProfile(token);

		String message=postService.deletePost(postId, reqUser.getId());

		MessageResponse res=new MessageResponse(message);

		return new ResponseEntity<MessageResponse> (res, HttpStatus.OK);

	}

	@PutMapping("/save_post/{postId}")
	public ResponseEntity<MessageResponse> savedPostHandler(@RequestHeader("Authorization")String token,@PathVariable Long postId) throws UserException, PostException{

		User user =userService.findUserProfile(token);
		String message=postService.savedPost(postId, user.getId());
		MessageResponse res=new MessageResponse(message);

		return new ResponseEntity<>(res,HttpStatus.OK);
	}

	@PutMapping("/unsave_post/{postId}")
	public ResponseEntity<MessageResponse> unSavedPostHandler(@RequestHeader("Authorization")String token,@PathVariable Long postId) throws UserException, PostException{

		User user =userService.findUserProfile(token);
		String message=postService.unSavePost(postId, user.getId());
		MessageResponse res=new MessageResponse(message);

		return new ResponseEntity<>(res,HttpStatus.OK);
	}

	@PutMapping("/edit")
	public ResponseEntity<MessageResponse> editPostHandler(@RequestBody Post post, @RequestHeader("Authorization") String token) throws PostException, UserException{
		User reqUser = userService.findUserProfile(token);
		postService.editPost(post, reqUser.getId());
		MessageResponse res=new MessageResponse("Post Updated Succefully");
		return new ResponseEntity<MessageResponse>(res,HttpStatus.OK);
	}

}
