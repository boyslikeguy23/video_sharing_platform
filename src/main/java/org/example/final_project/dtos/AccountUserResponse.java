package org.example.final_project.dtos;

import org.example.final_project.models.User;

public class AccountUserResponse extends PublicUserResponse {
    private String email;
    private String mobile;

    public static AccountUserResponse from(User user) {
        AccountUserResponse response = new AccountUserResponse();
        response.id = user.getId();
        response.username = user.getUsername();
        response.name = user.getName();
        response.image = user.getImage();
        response.bio = user.getBio();
        response.website = user.getWebsite();
        response.gender = user.getGender();
        response.followerCount = user.getFollower().size();
        response.followingCount = user.getFollowing().size();
        response.email = user.getEmail();
        response.mobile = user.getMobile();
        return response;
    }

    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
}
