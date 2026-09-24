package org.example.final_project.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequest {
    @Email(message = "Email must be valid")
    @Size(min = 1, max = 254, message = "Email must contain 1 to 254 characters")
    private String email;

    @Size(min = 3, max = 50, message = "Username must contain 3 to 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9._]+$", message = "Username may contain only letters, numbers, dots, and underscores")
    private String username;

    @Size(min = 1, max = 100, message = "Name must contain 1 to 100 characters")
    private String name;

    @Size(min = 1, max = 50, message = "Mobile must contain 1 to 50 characters")
    private String mobile;

    @Size(min = 1, max = 2_048, message = "Website must contain 1 to 2048 characters")
    private String website;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Size(min = 1, max = 50, message = "Gender must contain 1 to 50 characters")
    private String gender;

    @Size(min = 1, max = 2_048, message = "Image URL must contain 1 to 2048 characters")
    private String image;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
