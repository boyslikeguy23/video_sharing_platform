package org.example.final_project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @JsonBackReference
    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;

    @Size(min = 1, max = 200, message = "")
    @Column(nullable = false)
    private String comment;

    @CreatedDate
    private Instant createdAt;
}
