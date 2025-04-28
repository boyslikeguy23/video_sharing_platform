package org.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_count")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoCount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long countId;

    @OneToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    private Long viewCount = 0L;
    private Long likeCount = 0L;
    private Long dislikeCount = 0L;
}
