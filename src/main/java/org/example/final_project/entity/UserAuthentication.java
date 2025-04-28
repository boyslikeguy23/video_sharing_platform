package org.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserAuthentication", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthentication extends BaseEntity {

    @Id
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String username;

    private String password;

}

