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

@RestController
public class AuthController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserService userService;

	@GetMapping("/signin")
	public ResponseEntity<User> signinHandler(Authentication auth) throws BadCredentialsException{
		 try {
		        User user = userRepo.findByEmail(auth.getName())
		            .orElseThrow(() -> new BadCredentialsException("Invalid Username or password"));
		        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
		    } catch (BadCredentialsException ex) {
		        throw new BadCredentialsException("Invalid username or password");
		    }
	
	}
	@PostMapping("/signup")
	public ResponseEntity<User> registerUserHandler(@RequestBody User user) throws UserException {
		User createdUser=userService.registerUser(user);
		System.out.println("createdUser --- "+createdUser);
		return new ResponseEntity<User>(createdUser,HttpStatus.CREATED);
	}
}
