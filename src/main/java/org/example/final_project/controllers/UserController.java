package org.example.final_project.controllers;


import org.example.final_project.exceptions.UserException;
import org.example.final_project.dtos.AccountUserResponse;
import org.example.final_project.dtos.PublicUserResponse;
import org.example.final_project.dtos.UpdateUserRequest;
import org.example.final_project.models.User;
import org.example.final_project.responses.MessageResponse;
import org.example.final_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	@GetMapping("id/{id}")
	public ResponseEntity<PublicUserResponse> findUserByIdHandler(@PathVariable Long id) throws UserException {
		User user=userService.findUserById(id);
		return new ResponseEntity<>(PublicUserResponse.from(user),HttpStatus.OK);
	}
	
	
	@GetMapping("username/{username}")
	public ResponseEntity<PublicUserResponse> findByUsernameHandler(@PathVariable("username") String username) throws UserException{
		User user = userService.findUserByUsername(username);
		return new ResponseEntity<>(PublicUserResponse.from(user),HttpStatus.OK);
	}
	
	@PutMapping("/follow/{followUserId}")
	public ResponseEntity<MessageResponse> followUserHandler(@RequestHeader("Authorization") String token, @PathVariable Long followUserId) throws UserException{
		User reqUser=userService.findUserProfile(token);
		String message=userService.followUser(reqUser.getId(), followUserId);
		MessageResponse res=new MessageResponse(message);
		return new ResponseEntity<MessageResponse>(res,HttpStatus.OK);
	}
	
	@PutMapping("/unfollow/{unfollowUserId}")
	public ResponseEntity<MessageResponse> unfollowUserHandler(@RequestHeader("Authorization") String token, @PathVariable Long unfollowUserId) throws UserException{
		
		User reqUser=userService.findUserProfile(token);
		
		String message=userService.unfollowUser(reqUser.getId(), unfollowUserId);
		MessageResponse res=new MessageResponse(message);
		return new ResponseEntity<MessageResponse>(res,HttpStatus.OK);
	}

	@PutMapping("/remove-follower/{followerUserId}")
	public ResponseEntity<MessageResponse> removeFollowerHandler(
			@RequestHeader("Authorization") String token,
			@PathVariable Long followerUserId) throws UserException {

		User reqUser = userService.findUserProfile(token);
		String message = userService.removeFollower(reqUser.getId(), followerUserId);
		MessageResponse res = new MessageResponse(message);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@GetMapping("/req")
	public ResponseEntity<AccountUserResponse> findUserProfileHandler(@RequestHeader("Authorization") String token) throws UserException{
		
		User user=userService.findUserProfile(token);
		
		
		return new ResponseEntity<>(AccountUserResponse.from(user),HttpStatus.OK);
		

	}
	
	@GetMapping("/m/{userIds}")
	public ResponseEntity<List<PublicUserResponse>> findAllUsersByUserIdsHandler(@PathVariable List<Long> userIds){
		List<User> users=userService.findUsersByUserIds(userIds);
		return new ResponseEntity<>(toPublicProfiles(users),HttpStatus.OK);
		
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<PublicUserResponse>> searchUserHandler(@RequestParam("q")String query) throws UserException{
		
		List<User> users=userService.searchUser(query);
		
		return new ResponseEntity<>(toPublicProfiles(users),HttpStatus.OK);
	}
	
	@PutMapping("/account/edit")
	public ResponseEntity<AccountUserResponse> updateUser(@RequestHeader("Authorization") String token, @Valid @RequestBody UpdateUserRequest user) throws UserException{
		
		User reqUser=userService.findUserProfile(token);
		User updatedUser=userService.updateUserDetails(user, reqUser);
		
		return new ResponseEntity<>(AccountUserResponse.from(updatedUser),HttpStatus.OK);
		
	}

	@GetMapping("/{id}/following")
	public List<PublicUserResponse> getFollowing(@PathVariable Long id) {
		return toPublicProfiles(userService.getFollowingUsers(id));
	}

	@GetMapping("/{id}/follower")
	public List<PublicUserResponse> getFollowers(@PathVariable Long id) {
		return toPublicProfiles(userService.getFollowerUsers(id));
	}

	@GetMapping("/populer")
	public ResponseEntity<List<PublicUserResponse>> populerUsersHandler(){
		
		List<User> populerUsers=userService.popularUser();
		
		return new ResponseEntity<>(toPublicProfiles(populerUsers),HttpStatus.OK);
		
	}

	private List<PublicUserResponse> toPublicProfiles(List<User> users) {
		return users.stream().map(PublicUserResponse::from).collect(Collectors.toList());
	}

}
