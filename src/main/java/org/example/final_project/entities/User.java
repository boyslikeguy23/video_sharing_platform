package org.example.final_project.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 100)
    private String fullName;

    @Size(max = 100)
    private String nickName;

    private String password;

    @Size(max = 15)
    private String phone;

    private LocalDateTime dateOfBirth;

    private String avatarUrl;

    @Column(nullable = false)
    private boolean isActive = true;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private UserAuthentication userAuthentication;

    @NotNull(message = "Role ID cannot be null")
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL)
    private List<Video> videos;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole().getName()));
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return isActive();
    }
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Playlist> playlists;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Notification> notifications;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<SearchHistory> searchHistories;



}

