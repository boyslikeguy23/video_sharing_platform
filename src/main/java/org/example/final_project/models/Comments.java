package org.example.final_project.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.example.final_project.dtos.UserDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Comments {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Embedded
	@NotNull
	@AttributeOverride(name="id",column = @Column(name="user_id"))
	private UserDto userDto;
	
	@NotNull
	private String content;
	
	@Embedded
	@ElementCollection
	private Set<UserDto> likedByUsers= new HashSet<>();
	
	@ManyToOne
    @JoinColumn(name = "post_id")
	private Post post;
	
	private LocalDateTime createdAt;
	
	
	

	public Comments() {
		// TODO Auto-generated constructor stub
	}


	public Comments(Integer id, @NotNull UserDto userDto, @NotNull String content, Set<UserDto> likedByUsers, Post post,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.userDto = userDto;
		this.content = content;
		this.likedByUsers = likedByUsers;
		this.post = post;
		this.createdAt = createdAt;
	}


	public Set<UserDto> getLikedByUsers() {
		return likedByUsers;
	}


	public void setLikedByUsers(Set<UserDto> likedByUsers) {
		this.likedByUsers = likedByUsers;
	}


	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserDto getUserDto() {
		return userDto;
	}


	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}


	@Override
	public String toString() {
		return "Comments [id=" + id + ", userDto=" + userDto + ", content=" + content + ", likedByUsers=" + likedByUsers
				+ "]";
	}

	
	
	

}
