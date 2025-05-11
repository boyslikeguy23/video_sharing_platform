package org.example.final_project.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Data
@Table(name = "likes")
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @JsonBackReference
    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;


    @CreatedDate
    private Instant createAt;


}
