package org.example.final_project.payloads;


import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Getter
@Setter
public class UpdateUserDetails {

    @Pattern(regexp = "^[a-zA-Z]{5,15}$", message = "userName must be between 5 and 10 characters, containing only letters (no numbers or special characters).")
    private String UserName;
    private String name;
    private String bio;
}
