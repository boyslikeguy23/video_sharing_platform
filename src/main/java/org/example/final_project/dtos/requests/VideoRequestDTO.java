package org.example.final_project.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.final_project.enums.Privacy;

@Data
public class VideoRequestDTO {
    private String caption;
    @NotBlank(message = "Video URL khong duoc de trong")
    private String videoUrl;
    private Long createdByUserId; // ID của người tạo video
    private Privacy privacy;
}
