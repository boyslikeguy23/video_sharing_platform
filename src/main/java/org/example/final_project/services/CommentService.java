package org.example.final_project.services;



import org.example.final_project.exceptions.CommentException;
import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Comments;

import java.util.List;

public interface CommentService {
	
	public Comments createComment(Comments comment, Long postId, Long userId) throws PostException, UserException;

	public Comments findCommentById(Long commentId) throws CommentException;
	public Comments likeComment(Long CommentId,Long userId) throws UserException, CommentException;
	public Comments unlikeComment(Long CommentId,Long userId) throws UserException, CommentException;
	
	public String deleteCommentById(Long commentId) throws CommentException;
	
	public String editComment(Comments comment, Long commentId) throws CommentException;
	
	public List<Comments> findCommentByPostId(Long postId)throws PostException;
}
