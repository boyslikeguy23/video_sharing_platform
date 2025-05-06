package org.example.final_project.dtos.responses;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String email;
    private String fullName;
    private String nickName;
    private String phone;
    private LocalDateTime dateOfBirth;
    private String avatarUrl;
    private boolean isActive;
    private String roleName;
}
