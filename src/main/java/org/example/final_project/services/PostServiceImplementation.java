package org.example.final_project.services;


import org.example.final_project.dtos.UserDto;
import org.example.final_project.exceptions.PostException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Post;
import org.example.final_project.models.User;
import org.example.final_project.repositories.PostRepository;
import org.example.final_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class PostServiceImplementation implements PostService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostRepository postRepo;
	
	@Autowired
	private UserRepository userRepo;
	


	
	
	@Override
	public Post createPost(Post post, Long userId) throws UserException {
		
		User user = userService.findUserById(userId);
		
		UserDto userDto=new UserDto();
		
		userDto.setEmail(user.getEmail());
		userDto.setUsername(user.getUsername());
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setUserImage(user.getImage());
		
		post.setUser(userDto);
		
		post.setCreatedAt(LocalDateTime.now());
		
			Post createdPost =postRepo.save(post);
			
		
		return createdPost;
	}

	
	@Override
	public List<Post> findPostByUserId(Long userId) throws UserException {
		
		List<Post> posts=postRepo.findByUserId(userId);
		if(posts.isEmpty()){
			throw new UserException("No posts found");
		}
		return posts;
	}


	@Override
	public Post findePostById(Long postId) throws PostException {
		Optional<Post> opt = postRepo.findById(postId);
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new PostException("Post not exist with id: "+postId);
	}


	@Override
	public List<Post> findAllPost() throws PostException {
		List<Post> posts = postRepo.findAll();
		if(!posts.isEmpty()) {
			return posts;
		}
		throw new PostException("Post Not Exist");
	}


	@Override
	public Post likePost(Long postId, Long userId) throws UserException, PostException  {
		// TODO Auto-generated method stub
		
		User user= userService.findUserById(userId);
		
		UserDto userDto=new UserDto();
		
		userDto.setEmail(user.getEmail());
		userDto.setUsername(user.getUsername());
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setUserImage(user.getImage());
		
		Post post=findePostById(postId);
		post.getLikedByUsers().add(userDto);
	
	
		return postRepo.save(post);
		
		
	}

	@Override
	public Post unLikePost(Long postId, Long userId) throws UserException, PostException  {
		// TODO Auto-generated method stub
		
		User user= userService.findUserById(userId);
		UserDto userDto=new UserDto();
		
		userDto.setEmail(user.getEmail());
		userDto.setUsername(user.getUsername());
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setUserImage(user.getImage());
		
		Post post=findePostById(postId);
		post.getLikedByUsers().remove(userDto);
	
	
		
		return postRepo.save(post);
	}


	@Override
	public String deletePost(Long postId, Long userId) throws UserException, PostException {
		// TODO Auto-generated method stub
		
		Post post =findePostById(postId);
		
		User user=userService.findUserById(userId);
		System.out.println(post.getUser().getId()+" ------ "+user.getId());
		if(post.getUser().getId().equals(user.getId())) {
			System.out.println("inside delete");
			postRepo.deleteById(postId);
		
		return "Post Deleted Successfully";
		}
		
		
		throw new PostException("You Dont have access to delete this post");
		
	}


	@Override
	public List<Post> findAllPostByUserIds(List<Long> userIds) throws PostException, UserException {
		
		
		List<Post> posts= postRepo.findAllPostByUserIds(userIds);
		
		if(posts.isEmpty()) {
			throw new PostException("No Post Available of your followings");
		}
		
		
		return posts;
	}


	@Override
	public String savedPost(Long postId, Long userId) throws PostException, UserException {
		
		Post post=findePostById(postId);
		User user=userService.findUserById(userId);
		if(!user.getSavedPost().contains(post)) {
			user.getSavedPost().add(post);
			userRepo.save(user);
		}

		return "Post Saved Successfully";
	}


	@Override
	public String unSavePost(Long postId, Long userId) throws PostException, UserException {
		Post post=findePostById(postId);
		User user=userService.findUserById(userId);
		
		if(user.getSavedPost().contains(post)) {
			user.getSavedPost().remove(post);
			userRepo.save(user);
		}
		
		return "Post Remove Successfully";
	}


	@Override
	public Post editPost(Post post, Long userId) throws PostException {
		Post isPost=findePostById(post.getId());

		if (!isPost.getUser().getId().equals(userId)) {
			throw new PostException("You are not allowed to edit this post!");
		}
		if(post.getCaption()!=null) {
			isPost.setCaption(post.getCaption());
		}
		if(post.getLocation()!=null) {
			isPost.setLocation(post.getLocation());
		}
		
		return postRepo.save(isPost);
	}
	

}
