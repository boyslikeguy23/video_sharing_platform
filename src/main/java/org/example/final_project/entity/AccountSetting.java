package org.example.final_project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_settings")
public class AccountSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "language", length = 45)
    private String language;

    @Column(name = "location", length = 45)
    private String location;

    @Column(name = "is_restricted", columnDefinition = "TINYINT(1) default 0")
    private boolean isRestricted;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
