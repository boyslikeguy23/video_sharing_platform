package org.example.final_project.payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.final_project.model.Role;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @Size(min = 4, max = 10, message = "Password must be between 4 to 10 characters long ")
    private String name;
    @Pattern(regexp = "^[a-zA-Z]{5,15}$", message = "userName must be between 5 and 10 characters, containing only letters (no numbers or special characters).\n")
    @NotBlank(message = "userName must not be blank")
    private String username;
    @NotBlank
    @Size(min = 6, max = 6, message = "Password must be at least 6 characters long ")
    private String password;
    @Email(message = "Email address is not valid !!")
    @NotEmpty(message = "Email is required !!")
    private String email;
    private String bio;
    @CreatedDate
    @JsonIgnore
    private Instant createdAt;
    private String userProfileImage;
    @LastModifiedDate
    @JsonIgnore
    private Instant updatedAt;
    private boolean active;
    private Set<Role> roles = new HashSet<>();
//    private List<Post> posts = new ArrayList<>();
//    private Set<Follow> followers = new HashSet<>();
//    private Set<Follow> followings = new HashSet<>();
//    private Set<Chat> groups = new HashSet<>();
}
