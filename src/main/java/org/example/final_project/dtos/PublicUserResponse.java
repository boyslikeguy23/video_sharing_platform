package org.example.final_project.dtos;

import org.example.final_project.models.User;

public class PublicUserResponse {
    protected Long id;
    protected String username;
    protected String name;
    protected String image;
    protected String bio;
    protected String website;
    protected String gender;
    protected int followerCount;
    protected int followingCount;

    public static PublicUserResponse from(User user) {
        PublicUserResponse response = new PublicUserResponse();
        response.id = user.getId();
        response.username = user.getUsername();
        response.name = user.getName();
        response.image = user.getImage();
        response.bio = user.getBio();
        response.website = user.getWebsite();
        response.gender = user.getGender();
        response.followerCount = user.getFollower().size();
        response.followingCount = user.getFollowing().size();
        return response;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getBio() { return bio; }
    public String getWebsite() { return website; }
    public String getGender() { return gender; }
    public int getFollowerCount() { return followerCount; }
    public int getFollowingCount() { return followingCount; }
}
