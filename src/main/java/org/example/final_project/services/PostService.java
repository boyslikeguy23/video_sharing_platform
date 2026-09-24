package org.example.final_project.services;



import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.dtos.CreatePostRequest;
import org.example.final_project.dtos.CursorPage;
import org.example.final_project.models.Post;

import java.util.List;

public interface PostService {

	public Post createPost(CreatePostRequest request, Long userId) throws UserException;
	
	public Post editPost(Post post,Long userId) throws PostException;
	
	public String deletePost(Long postId, Long userId) throws UserException,PostException;
	
	public CursorPage<Post> findPostByUserId(Long userId, int size, String cursor);
	
	public Post findePostById(Long postId) throws PostException;
	
	public CursorPage<Post> findAllPost(int size, String cursor);
	
	public CursorPage<Post> findAllPostByUserIds(List<Long> userIds, int size, String cursor);
	
	public String savedPost(Long postId,Long userId) throws PostException, UserException;
	
	public String unSavePost(Long postId,Long userId) throws PostException, UserException;
		
	public Post likePost(Long postId ,Long userId) throws UserException, PostException;
	
	public Post unLikePost(Long postId ,Long userId) throws UserException, PostException;
	
}
