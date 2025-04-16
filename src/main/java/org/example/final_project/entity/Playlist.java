package org.example.final_project.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlist")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlaylistID")
    private Long playlistId;

    @Column(name = "PlaylistTitle", length = 255)
    private String playlistTitle;

    @Column(name = "UserID")
    private Long userId;

    // Quan hệ với PlaylistVideo
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistVideo> playlistVideos = new ArrayList<>();
}
