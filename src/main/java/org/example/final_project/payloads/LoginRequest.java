package org.example.final_project.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
