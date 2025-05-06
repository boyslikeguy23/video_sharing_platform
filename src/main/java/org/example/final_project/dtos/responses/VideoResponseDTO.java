package org.example.final_project.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoResponseDTO {
    private Long videoId;
    private String caption;
    private String videoUrl;
    private String uploaderName;
    private Long viewCount;
    private Long likeCount;
}
