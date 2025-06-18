package org.example.final_project.services;


import org.example.final_project.dtos.UserDto;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Comments;
import org.example.final_project.models.Post;
import org.example.final_project.models.Story;
import org.example.final_project.models.User;
import org.example.final_project.repositories.CommentRepository;
import org.example.final_project.repositories.PostRepository;
import org.example.final_project.repositories.StoryRepository;
import org.example.final_project.repositories.UserRepository;
import org.example.final_project.security.JwtTokenClaims;
import org.example.final_project.security.JwtTokenProvider;
import org.example.final_project.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Autowired
//	private PostService postService;

 @Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private StoryRepository storyRepository;

	@Override
	public User registerUser(User user) throws UserException {

		System.out.println("registered user ------ ");

		Optional<User> isEmailExist = repo.findByEmail(user.getEmail());

		if (isEmailExist.isPresent()) {
			throw new UserException("Email đã được sử dụng");
		}

		Optional<User> isUsernameTaken=repo.findByUsername(user.getUsername());

		if(isUsernameTaken.isPresent()) {
			throw new UserException("Username đã được sử dụng");
		}

		if(user.getEmail()== null || user.getPassword()== null || user.getUsername()==null || user.getName()==null) {
			throw new UserException("Vui lòng cung cấp đầy đủ thông tin đăng ký");

		}

		String encodedPassword=passwordEncoder.encode(user.getPassword());

		User newUser=new User();

		newUser.setEmail(user.getEmail());
		newUser.setPassword(encodedPassword);
		newUser.setUsername(user.getUsername());
		newUser.setName(user.getName());

		return repo.save(newUser);

	}


	@Override
	public User findUserById(Long userId) throws UserException {

		Optional<User> opt =repo.findById(userId);

		if(opt.isPresent()) {
			return opt.get();
		}

		throw new UserException("Không tìm thấy người dùng với id :"+userId);
	}




	@Override
	public String followUser(Long reqUserId, Long followUserId) throws UserException {
		User followUser=findUserById(followUserId);
		User reqUser=findUserById(reqUserId);

		UserDto follower=new UserDto();
		follower.setEmail(reqUser.getEmail());
		follower.setUsername(reqUser.getUsername());
		follower.setId(reqUser.getId());
		follower.setName(reqUser.getName());
		follower.setUserImage(reqUser.getImage());


		UserDto following=new UserDto();
		following.setEmail(followUser.getEmail());
		following.setUsername(followUser.getUsername());
		following.setId(followUser.getId());
		following.setName(followUser.getName());
		following.setUserImage(followUser.getImage());


		followUser.getFollower().add(follower);
		reqUser.getFollowing().add(following);

		repo.save(followUser);
		repo.save(reqUser);

		return "Ban đang theo dõi "+followUser.getUsername();
	}


	@Override
	public String unfollowUser(Long reqUserId, Long unfollowUserId) throws UserException {


		User unfollowUser=findUserById(unfollowUserId);

		System.out.println("Bỏ theo dõi người dùng ---- "+unfollowUser.toString());
		System.out.println("Bỏ theo dõi người theo dõi "+unfollowUser.getFollower().toString());

		User reqUser=findUserById(reqUserId);

		UserDto unfollow=new UserDto();
		unfollow.setEmail(reqUser.getEmail());
		unfollow.setUsername(reqUser.getUsername());
		unfollow.setId(reqUser.getId());
		unfollow.setName(reqUser.getName());
		unfollow.setUserImage(reqUser.getImage());


		UserDto following=new UserDto();
		following.setEmail(unfollowUser.getEmail());
		following.setUsername(unfollowUser.getUsername());
		following.setId(unfollowUser.getId());
		following.setName(unfollowUser.getName());
		following.setUserImage(unfollowUser.getImage());


		unfollowUser.getFollower().remove(unfollow);
		reqUser.getFollowing().remove(following);

		repo.save(reqUser);
		repo.save(unfollowUser);

//		User user= userService.findUserById(userId);
//		UserDto userDto=new UserDto();
//		
//		userDto.setEmail(user.getEmail());
//		userDto.setUsername(user.getUsername());
//		userDto.setId(user.getId());
//		
//		Post post=findePostById(postId);
//		post.getLikedByUsers().remove(userDto);

		return "Bạn đã bỏ theo dõi "+unfollowUser.getUsername();

	}
	@Override
	public String removeFollower(Long reqUserId, Long followerUserId) throws UserException {
		User reqUser = findUserById(reqUserId); // Người bị theo dõi (chính mình)
		User followerUser = findUserById(followerUserId); // Người follower

		// Tạo UserDto cho follower
		UserDto followerDto = new UserDto();
		followerDto.setEmail(followerUser.getEmail());
		followerDto.setUsername(followerUser.getUsername());
		followerDto.setId(followerUser.getId());
		followerDto.setName(followerUser.getName());
		followerDto.setUserImage(followerUser.getImage());

		// Tạo UserDto cho following
		UserDto followingDto = new UserDto();
		followingDto.setEmail(reqUser.getEmail());
		followingDto.setUsername(reqUser.getUsername());
		followingDto.setId(reqUser.getId());
		followingDto.setName(reqUser.getName());
		followingDto.setUserImage(reqUser.getImage());

		// Xoá follower khỏi danh sách follower của mình
		reqUser.getFollower().remove(followerDto);
		// Xoá mình khỏi danh sách following của người kia
		followerUser.getFollowing().remove(followingDto);

		repo.save(reqUser);
		repo.save(followerUser);

		return "Đã xoá người theo dõi " + followerUser.getUsername();
	}


	@Override
	public User findUserProfile(String token) throws UserException {

		token=token.substring(7);

	    JwtTokenClaims jwtTokenClaims = jwtTokenProvider.getClaimsFromToken(token);

	    String username = jwtTokenClaims.getUsername();

	    Optional<User> opt = repo.findByEmail(username);

	    if(opt.isPresent()) {


	    	return opt.get();

	    }

	    throw new UserException("Người dùng không tồn tại : "+username);




	}


	@Override
	public User findUserByUsername(String username) throws UserException {

		Optional<User> opt=repo.findByUsername(username);

		if(opt.isPresent()) {
			User user=opt.get();
			return user;
		}

		throw new UserException("user not exist with username "+username);
	}


	@Override
	public List<User> findUsersByUserIds(List<Long> userIds) {
		List<User> users= repo.findAllUserByUserIds(userIds);

		return users;
	}


	@Override
	public List<User> searchUser(String query) throws UserException {
		List<User> users=repo.findByQuery(query);
		if(users.size()==0) {
			throw new UserException("user not exist");
		}
		return users;
	}


	@Override
	public User updateUserDetails(User updatedUser, User existingUser) throws UserException {

		boolean usernameChanged = false;
		boolean nameChanged = false;
		boolean imageChanged = false;
		boolean emailChanged = false;

		if(updatedUser.getEmail()!= null) {
			emailChanged = !updatedUser.getEmail().equals(existingUser.getEmail());
			existingUser.setEmail(updatedUser.getEmail());	
		}
		if(updatedUser.getBio()!=null) {
			existingUser.setBio(updatedUser.getBio());
		}
		if(updatedUser.getName()!=null) {
			nameChanged = !updatedUser.getName().equals(existingUser.getName());
			existingUser.setName(updatedUser.getName());
		}
		if(updatedUser.getUsername()!=null) {
			usernameChanged = !updatedUser.getUsername().equals(existingUser.getUsername());
			existingUser.setUsername(updatedUser.getUsername());
		}
		if(updatedUser.getMobile()!=null) {
			existingUser.setMobile(updatedUser.getMobile());
		}
		if(updatedUser.getGender()!=null) {
			existingUser.setGender(updatedUser.getGender());
		}
		if(updatedUser.getWebsite()!=null) {
			existingUser.setWebsite(updatedUser.getWebsite());
		}
		if(updatedUser.getImage()!=null) {
			imageChanged = !updatedUser.getImage().equals(existingUser.getImage());
			existingUser.setImage(updatedUser.getImage());
		}


		if(!updatedUser.getId().equals(existingUser.getId())) {
			System.out.println(" u "+updatedUser.getId()+" e "+existingUser.getId());
			throw new UserException("you can't update another user"); 
		}

		// Save the user first to ensure it's updated
		User savedUser = repo.save(existingUser);

		// Only update related models if relevant fields have changed
		if (usernameChanged || nameChanged || imageChanged || emailChanged) {
			updateUserInRelatedModels(savedUser);
		}

		return savedUser;
	}

	/**
	 * Updates user information in all related models
	 */
	private void updateUserInRelatedModels(User user) {
		Long userId = user.getId();

		// Update Posts
		List<Post> userPosts = postRepository.findByUserId(userId);
		for (Post post : userPosts) {
			UserDto postUser = post.getUser();
			postUser.setUsername(user.getUsername());
			postUser.setName(user.getName());
			postUser.setEmail(user.getEmail());
			postUser.setUserImage(user.getImage());
			postRepository.save(post);
		}

		// Update Comments
		List<Post> allPosts = postRepository.findAll();
		for (Post post : allPosts) {
			List<Comments> comments = post.getComments();
			boolean commentsUpdated = false;

			for (Comments comment : comments) {
				// Update comment author
				if (comment.getUserDto() != null && comment.getUserDto().getId().equals(userId)) {
					UserDto commentUser = comment.getUserDto();
					commentUser.setUsername(user.getUsername());
					commentUser.setName(user.getName());
					commentUser.setEmail(user.getEmail());
					commentUser.setUserImage(user.getImage());
					commentsUpdated = true;
				}

				// Update users who liked the comment
				Set<UserDto> likedByUsers = comment.getLikedByUsers();
				for (UserDto likedByUser : likedByUsers) {
					if (likedByUser.getId().equals(userId)) {
						likedByUser.setUsername(user.getUsername());
						likedByUser.setName(user.getName());
						likedByUser.setEmail(user.getEmail());
						likedByUser.setUserImage(user.getImage());
						commentsUpdated = true;
					}
				}
			}

			// Only save the post if comments were updated
			if (commentsUpdated) {
				postRepository.save(post);
			}
		}

		// Update Stories
		List<Story> userStories = storyRepository.findAllStoriesByUserId(userId);
		for (Story story : userStories) {
			UserDto storyUser = story.getUserDto();
			storyUser.setUsername(user.getUsername());
			storyUser.setName(user.getName());
			storyUser.setEmail(user.getEmail());
			storyUser.setUserImage(user.getImage());
			storyRepository.save(story);
		}

		// Update users who liked posts
		List<Post> allPostsForLikes = postRepository.findAll();
		for (Post post : allPostsForLikes) {
			boolean likesUpdated = false;
			Set<UserDto> likedByUsers = post.getLikedByUsers();

			for (UserDto likedByUser : likedByUsers) {
				if (likedByUser.getId().equals(userId)) {
					likedByUser.setUsername(user.getUsername());
					likedByUser.setName(user.getName());
					likedByUser.setEmail(user.getEmail());
					likedByUser.setUserImage(user.getImage());
					likesUpdated = true;
				}
			}

			// Only save the post if likes were updated
			if (likesUpdated) {
				postRepository.save(post);
			}
		}

		// Update follower/following relationships in all users
		List<User> allUsers = repo.findAll();
		for (User otherUser : allUsers) {
			boolean userUpdated = false;

			// Update in follower set
			Set<UserDto> followers = otherUser.getFollower();
			for (UserDto follower : followers) {
				if (follower.getId().equals(userId)) {
					follower.setUsername(user.getUsername());
					follower.setName(user.getName());
					follower.setEmail(user.getEmail());
					follower.setUserImage(user.getImage());
					userUpdated = true;
				}
			}

			// Update in following set
			Set<UserDto> following = otherUser.getFollowing();
			for (UserDto followedUser : following) {
				if (followedUser.getId().equals(userId)) {
					followedUser.setUsername(user.getUsername());
					followedUser.setName(user.getName());
					followedUser.setEmail(user.getEmail());
					followedUser.setUserImage(user.getImage());
					userUpdated = true;
				}
			}

			// Only save the user if follower/following were updated
			if (userUpdated) {
				repo.save(otherUser);
			}
		}
	}

	public List<User> getFollowingUsers(Long userId) {
		User user = repo.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		Set<UserDto> following = user.getFollowing();
		List<Long> followingIds = following.stream()
				.map(UserDto::getId)
				.collect(Collectors.toList());
		if (followingIds.isEmpty()) return new ArrayList<>();
		return repo.findAllUserByUserIds(followingIds);
	}

	public List<User> getFollowerUsers(Long userId) {
		User user = repo.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		Set<UserDto> follower = user.getFollower();
		List<Long> followerIds = follower.stream()
				.map(UserDto::getId)
				.collect(Collectors.toList());
		if (followerIds.isEmpty()) return new ArrayList<>();
		return repo.findAllUserByUserIds(followerIds);
	}


	@Override
	public List<User> popularUser() {
		List<User> users = repo.findAll();

		UserUtil.sortUserByNumberOfPost(users);

		int numUsers = Math.min(users.size(), 5); 
		List<User> populerUsers = users.subList(0, numUsers);

		return populerUsers;

	}






}
