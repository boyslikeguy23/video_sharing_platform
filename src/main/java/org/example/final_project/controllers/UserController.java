package org.example.final_project.controllers;


import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.User;
import org.example.final_project.responses.MessageResponse;
import org.example.final_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	@GetMapping("id/{id}")
	public ResponseEntity<User> findUserByIdHandler(@PathVariable Long id) throws UserException {
		User user=userService.findUserById(id);
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
	
	
	@GetMapping("username/{username}")
	public ResponseEntity<User> findByUsernameHandler(@PathVariable("username") String username) throws UserException{
		User user = userService.findUserByUsername(username);
		return new ResponseEntity<User>(user,HttpStatus.ACCEPTED);
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
	public ResponseEntity<User> findUserProfileHandler(@RequestHeader("Authorization") String token) throws UserException{
		
		User user=userService.findUserProfile(token);
		
		
		return new ResponseEntity<User>(user,HttpStatus.ACCEPTED);
		

	}
	
	@GetMapping("/m/{userIds}")
	public ResponseEntity<List<User>> findAllUsersByUserIdsHandler(@PathVariable List<Long> userIds){
		List<User> users=userService.findUsersByUserIds(userIds);
		
		System.out.println("userIds ------ "+userIds);
		return new ResponseEntity<List<User>>(users,HttpStatus.ACCEPTED);
		
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<User>> searchUserHandler(@RequestParam("q")String query) throws UserException{
		
		List<User> users=userService.searchUser(query);
		
		return new ResponseEntity<List<User>>(users,HttpStatus.OK);
	}
	
	@PutMapping("/account/edit")
	public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String token, @RequestBody User user) throws UserException{
		
		User reqUser=userService.findUserProfile(token);
		User updatedUser=userService.updateUserDetails(user, reqUser);
		
		return new ResponseEntity<User>(updatedUser,HttpStatus.OK);
		
	}

	@GetMapping("/{id}/following")
	public List<User> getFollowing(@PathVariable Long id) {
		return userService.getFollowingUsers(id);
	}

	@GetMapping("/{id}/follower")
	public List<User> getFollowers(@PathVariable Long id) {
		return userService.getFollowerUsers(id);
	}

	@GetMapping("/populer")
	public ResponseEntity<List<User>> populerUsersHandler(){
		
		List<User> populerUsers=userService.popularUser();
		
		return new ResponseEntity<List<User>>(populerUsers,HttpStatus.OK);
		
	}

}
