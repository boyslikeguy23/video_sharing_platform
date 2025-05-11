package org.example.final_project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    //    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    //    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    //    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

    @NotBlank
    @Column(nullable = false)
    private String imageUrl;

    private String caption;
}
