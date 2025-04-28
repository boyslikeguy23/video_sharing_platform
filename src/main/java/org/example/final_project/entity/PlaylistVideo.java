package org.example.final_project.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "playlist_video")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistVideo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistVideoId;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    private Long orderIndex;
}