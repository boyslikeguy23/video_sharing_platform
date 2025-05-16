package org.example.final_project.services;



import org.example.final_project.exceptions.CommentException;
import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Comments;

import java.util.List;

public interface CommentService {
	
	public Comments createComment(Comments comment, Integer postId, Integer userId) throws PostException, UserException;

	public Comments findCommentById(Integer commentId) throws CommentException;
	public Comments likeComment(Integer CommentId,Integer userId) throws UserException, CommentException;
	public Comments unlikeComment(Integer CommentId,Integer userId) throws UserException, CommentException;
	
	public String deleteCommentById(Integer commentId) throws CommentException;
	
	public String editComment(Comments comment, Integer commentId) throws CommentException;
	
	public List<Comments> findCommentByPostId(Integer postId)throws PostException;
}
