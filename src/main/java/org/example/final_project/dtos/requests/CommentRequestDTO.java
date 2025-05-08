package org.example.final_project.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDTO {
    @NotBlank
    private String content;

    @NotNull
    private Long videoId;

    @NotNull
    private Long userId;

    private Long parentCommentId; // optional
}