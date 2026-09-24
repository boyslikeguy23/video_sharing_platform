package org.example.final_project.services;


import org.example.final_project.dtos.UserDto;
import org.example.final_project.exceptions.CommentException;
import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Comments;
import org.example.final_project.models.Post;
import org.example.final_project.models.User;
import org.example.final_project.repositories.CommentRepository;
import org.example.final_project.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentsServiceImplement implements CommentService {
	
	@Autowired
	private CommentRepository repo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private PostRepository postRepo;

	
	
	@Override
	public Comments createComment(Comments comment, Long postId, Long userId) throws PostException, UserException {
		
		User user=userService.findUserById(userId);
		
		Post post=postService.findePostById(postId);
		
		// TODO Auto-generated method stub
		
		UserDto userDto=new UserDto();
		userDto.setEmail(user.getEmail());
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setName(user.getName());
		userDto.setUserImage(user.getImage());
		
		// Only copy client-editable fields; a supplied ID must never update an existing comment.
		Comments created = new Comments();
		created.setContent(comment.getContent());
		created.setUserDto(userDto);
		created.setCreatedAt(LocalDateTime.now());
		created.setPost(post);
		
		Comments newComment= repo.save(created);
		
		post.getComments().add(newComment);
		
		postRepo.save(post);
		
		return newComment;
	}


	@Override
	public Comments findCommentById(Long commentId) throws CommentException {
		Optional<Comments> opt=repo.findById(commentId);
		
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new CommentException("comment not exist with id : "+commentId);
	}

	@Override
	public Comments likeComment(Long commentId, Long userId) throws UserException, CommentException {
		// TODO Auto-generated method stub
		
		User user=userService.findUserById(userId);
		Comments comment=findCommentById(commentId);
		
		
		UserDto userDto=new UserDto();
		userDto.setEmail(user.getEmail());
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setName(user.getName());
		userDto.setUserImage(user.getImage());
		
		comment.getLikedByUsers().add(userDto);
		System.out.println(("like ------- "+" ------ "+comment));
		return repo.save(comment);
		
	}


	@Override
	public Comments unlikeComment(Long commentId, Long userId) throws UserException, CommentException {
		User user = userService.findUserById(userId);
		Comments comment = findCommentById(commentId);

		// Tạo UserDto giống như khi like
		UserDto userDto = new UserDto();
		userDto.setEmail(user.getEmail());
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setName(user.getName());
		userDto.setUserImage(user.getImage());

		// Remove userDto khỏi likedByUsers
		comment.getLikedByUsers().removeIf(u -> u.getId().equals(userDto.getId()));

		return repo.save(comment);
	}


	@Override
	public String deleteCommentById(Long commentId, Long userId) throws CommentException {
		Comments comment=findCommentById(commentId);
		requireOwner(comment, userId);
		
		repo.deleteById(comment.getId());
		
		return "Comment Deleted Successfully";
	}


	@Override
	public String editComment(Comments comment, Long commentId, Long userId) throws CommentException {
		Comments isComment=findCommentById(commentId);
		requireOwner(isComment, userId);
		
		if(comment.getContent()!=null) {
			isComment.setContent(comment.getContent());
		}
		repo.save(isComment);
		return "Comment Updated Successfully";
	}

	private void requireOwner(Comments comment, Long userId) {
		if (userId == null || comment.getUserDto() == null || !userId.equals(comment.getUserDto().getId())) {
			throw new AccessDeniedException("Only the comment author can edit or delete this comment");
		}
	}


	@Override
	public List<Comments> findCommentByPostId(Long postId) throws PostException {
		List<Comments> comments =repo.findCommentsByPostId(postId);
		return comments;
	}




	
	
	
	
	

}
