package org.example.final_project.entity;

import jakarta.persistence.*;
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
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long videoId;

    String title;

    String description;

    String contentType;

    String videoUrl;

    double videoSize;

    String fileType;
}
