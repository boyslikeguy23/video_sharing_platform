package org.example.final_project.services;



import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.User;

import java.util.List;

public interface UserService {
	
	public User registerUser(User user) throws UserException;
	
	public User findUserById(Integer userId) throws UserException;
	
	public User findUserProfile(String token) throws UserException;
	
	public User findUserByUsername(String username) throws UserException;
	
	public String followUser(Integer reqUserId,Integer followUserId) throws UserException;
	
	public String unfollowUser(Integer reqUserId, Integer unfollowUserId) throws UserException; 
	
	public List<User> findUsersByUserIds(List<Integer> userIds);
	
	public List<User> searchUser(String query) throws UserException;
	
	public List<User> popularUser();
	
	

	public User updateUserDetails(User updatedUser, User existingUser) throws UserException;
	
	
}
