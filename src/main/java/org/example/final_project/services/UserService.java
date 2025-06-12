package org.example.final_project.services;



import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.User;

import java.util.List;

public interface UserService {
	
	public User registerUser(User user) throws UserException;
	
	public User findUserById(Long userId) throws UserException;
	
	public User findUserProfile(String token) throws UserException;
	
	public User findUserByUsername(String username) throws UserException;
	
	public String followUser(Long reqUserId,Long followUserId) throws UserException;
	
	public String unfollowUser(Long reqUserId, Long unfollowUserId) throws UserException;

	public String removeFollower(Long reqUserId, Long followerUserId) throws UserException;

	public List<User> findUsersByUserIds(List<Long> userIds);
	
	public List<User> searchUser(String query) throws UserException;
	
	public List<User> popularUser();

	public List<User> getFollowingUsers(Long userId);

	public List<User> getFollowerUsers(Long userId);

	public User updateUserDetails(User updatedUser, User existingUser) throws UserException;
	
	
}
