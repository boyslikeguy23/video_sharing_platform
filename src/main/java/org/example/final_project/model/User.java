package org.example.final_project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 4, max = 10, message = "Password must be between 4 to 10 characters long ")
    private String name;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[a-zA-Z]{5,15}$", message = "username must be between 5 and 10 characters, containing only letters (no numbers or special characters).\n")
    @NotBlank(message = "userName must not be blank")
    private String username;

    @Column(nullable = false)
//    @NotBlank
    private String password;

    @Column(nullable = false, unique = true)
    @Email(message = "Email address is not valid !!")
    @NotEmpty(message = "Email is required !!")
    private String email;

    private String bio;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;


    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'default.png'")
    private String userProfileImage;

    private boolean active;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

//    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
//    @JsonManagedReference
//    private List<Post> posts = new ArrayList<>();

    //    @JsonManagedReference
//    @OneToMany(mappedBy = "follower", fetch = FetchType.EAGER)
//    private Set<Follow> followers = new HashSet<>();

    ////    @JsonManagedReference
//    @OneToMany(mappedBy = "following", fetch = FetchType.EAGER)
//    private Set<Follow> followings = new HashSet<>();

//    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
//    private Set<Chat> chats = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(role ->
                new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }


    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

}
