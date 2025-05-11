package org.example.final_project.payloads;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Data
public class CommentRequest {
    @Size(min = 1,max = 200)
    @NotBlank
    private String comment;
}
