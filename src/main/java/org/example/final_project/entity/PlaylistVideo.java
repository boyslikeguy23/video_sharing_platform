package org.example.final_project.entity;

import jakarta.persistence.*;

public class PlaylistVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Có thể thêm ID riêng nếu cần

    @ManyToOne
    @JoinColumn(name = "PlaylistID", nullable = false)
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "VideoID", nullable = false)
    private Video video;
}
