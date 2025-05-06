package org.example.final_project.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    @Size(max = 100)
    private String fullName;

    @Size(max = 100)
    private String nickName;

    @Size(max = 15)
    private String phone;

    private LocalDateTime dateOfBirth;

    private String avatarUrl;

    private boolean isActive = true;

    @NotNull
    private Long roleId;
}
