package org.example.final_project.controllers;


import org.example.final_project.dtos.UserDto;
import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Reels;
import org.example.final_project.models.User;
import org.example.final_project.responses.MessageResponse;
import org.example.final_project.services.ReelService;
import org.example.final_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reels")
public class ReelController {
	
	@Autowired
	private ReelService reelService;
	
	@Autowired
	private UserService userService;
	

	
	@PostMapping("/create")
	public ResponseEntity<Reels> createPostHandler(@RequestBody Reels reel, @RequestHeader("Authorization") String token) throws UserException {
		
		
		User user=userService.findUserProfile(token);
		
		UserDto userDto=new UserDto();
		
		userDto.setEmail(user.getEmail());
		userDto.setUsername(user.getUsername());
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setUserImage(user.getImage());
		
		reel.setUser(userDto);
		
		System.out.println("create reel------"+reel.getVideo());
		
		Reels createdReel=reelService.createReels(reel);
		

		
		return new ResponseEntity<Reels>(createdReel, HttpStatus.CREATED);
	}
	
	@GetMapping("/")
	public ResponseEntity<List<Reels>> getAllReelHandler(){
		
		System.out.println("get all reel -------- ");
		List<Reels> reels=reelService.getAllReels();
		
		return new ResponseEntity<List<Reels>>(reels,HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/delete/{reelId}")
	public ResponseEntity<MessageResponse> deleteReelHandler(@PathVariable Integer reelId) throws PostException {
		
		reelService.deleteReels(reelId);
		
		MessageResponse res=new MessageResponse("Reel Deleted Succefully");
		
		return new ResponseEntity<MessageResponse> (res, HttpStatus.OK);
		
	}

}
