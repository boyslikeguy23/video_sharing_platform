package org.example.final_project.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private Long id;
    private String content;
    private Long videoId;
    private Long userId;
    private Long parentCommentId;
    private LocalDateTime commentedAt;
}