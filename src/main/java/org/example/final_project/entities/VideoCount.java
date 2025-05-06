package org.example.final_project.entities;

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
    private Long id;

    @OneToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;
    
    private int viewCount;
    private int likeCount;
    private int dislikeCount;

    public VideoCount(int i, int i1, Video saved) {
        super();
    }
}
