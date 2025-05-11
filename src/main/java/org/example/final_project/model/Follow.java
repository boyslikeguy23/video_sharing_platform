package org.example.final_project.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "follower")
    private User follower;

    //    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "following")
    private User following;

}


