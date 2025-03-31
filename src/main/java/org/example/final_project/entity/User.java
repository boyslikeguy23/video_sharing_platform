package org.example.final_project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
@Getter
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", length = 100, columnDefinition = "varchar(100) default ''")
    private String fullName;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    @Column(name = "email", length = 120, nullable = false, unique = true)
    private String email;

    @Column(name = "date_of_birth", columnDefinition = "date")
    private LocalDate dateOfBirth;


    @Column(name = "address", length = 200, columnDefinition = "varchar(200) default ''")
    private String address;

    @Column(name = "phone_number", length = 15, columnDefinition = "varchar(15) default ''")
    private String phoneNumber;

    @Column(name = "gender")
    private Boolean gender;

    @NotNull(message = "Role ID cannot be null")
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;



//    public int isActive() {
//        return this.isActive;
//    }
}
