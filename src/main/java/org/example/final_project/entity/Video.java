package org.example.final_project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "video")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;

//    @NotNull
//    @Column(nullable = false)
//    private String title;

    private String caption;

    @Column(nullable = false)
    private String videoUrl;

//    private String thumbnailUrl;

//    private LocalDateTime publishDate;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;


    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Reaction> reactions;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<VideoViewHistory> viewHistories;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL)
    private VideoCount videoCount;
}
