package org.example.final_project.services;



import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Post;

import java.util.List;

public interface PostService {

	public Post createPost(Post post, Long userId) throws UserException;
	
	public Post editPost(Post post,Long userId) throws PostException;
	
	public String deletePost(Long postId, Long userId) throws UserException,PostException;
	
	public List<Post> findPostByUserId(Long userId) throws UserException;
	
	public Post findePostById(Long postId) throws PostException;
	
	public List<Post> findAllPost() throws PostException;
	
	public List<Post> findAllPostByUserIds(List<Long> userIds) throws PostException, UserException;
	
	public String savedPost(Long postId,Long userId) throws PostException, UserException;
	
	public String unSavePost(Long postId,Long userId) throws PostException, UserException;
		
	public Post likePost(Long postId ,Long userId) throws UserException, PostException;
	
	public Post unLikePost(Long postId ,Long userId) throws UserException, PostException;
	
}
