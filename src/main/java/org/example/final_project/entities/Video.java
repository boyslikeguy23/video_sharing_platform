package org.example.final_project.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.final_project.enums.Privacy;

import java.util.List;

@Entity
@Table(name = "video")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

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

    @Column(name = "privacy", nullable = false)
    @Enumerated(EnumType.STRING)
    private Privacy privacy;

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
