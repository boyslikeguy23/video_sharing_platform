package org.example.final_project.controllers;


import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.User;
import org.example.final_project.repositories.UserRepository;
import org.example.final_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserService userService;

 @GetMapping("/signin")
	public ResponseEntity<?> signinHandler(Authentication auth) {
		 try {
		        User user = userRepo.findByEmail(auth.getName())
		            .orElseThrow(() -> new BadCredentialsException("Invalid Username or password"));
		        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
		    } catch (BadCredentialsException ex) {
		        return new ResponseEntity<>(Map.of("error", "Invalid username or password"), HttpStatus.UNAUTHORIZED);
		    }
	}
	@PostMapping("/signup")
	public ResponseEntity<?> registerUserHandler(@RequestBody User user) {
		try {
			User createdUser = userService.registerUser(user);
			return new ResponseEntity<User>(createdUser, HttpStatus.CREATED);
		} catch (UserException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
