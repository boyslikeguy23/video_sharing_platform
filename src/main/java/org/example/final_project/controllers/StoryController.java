package org.example.final_project.controllers;


import org.example.final_project.exceptions.StoryException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.dtos.CreateStoryRequest;
import org.example.final_project.models.Story;
import org.example.final_project.models.User;
import org.example.final_project.services.StoryService;
import org.example.final_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
	
	@Autowired
	private StoryService storyService;
	
	@Autowired
	private UserService userService;
	
	
	@PostMapping("/create")
	public ResponseEntity<Story> createStoryHandler(@Valid @RequestBody CreateStoryRequest request, @RequestHeader("Authorization") String token) throws UserException {
		
		User reqUser=userService.findUserProfile(token);
		
		Story createdStory =storyService.createStory(request, reqUser.getId());
		return new ResponseEntity<Story>(createdStory,HttpStatus.OK);
	}
	
	
	@GetMapping("/{userId}")
	public ResponseEntity<List<Story>> findAllStoryByUserIdHandler(@PathVariable Long userId) throws UserException, StoryException {
		
		List<Story> stories= storyService.findStoryByUserId(userId);
		
		return new ResponseEntity<List<Story>>(stories,HttpStatus.OK);
	}

}
