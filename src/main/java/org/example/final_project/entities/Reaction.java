package org.example.final_project.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reaction", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "video_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    private Boolean isLike; // true = like, false = dislike
}
