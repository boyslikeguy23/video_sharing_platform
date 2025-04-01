package org.example.final_project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "video")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Video extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long videoId;

    String title;

    String description;

    String videoUrl;

    double videoSize;

    String contentType;

    String fileType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    String videoScope;

    String videoPath;


}
