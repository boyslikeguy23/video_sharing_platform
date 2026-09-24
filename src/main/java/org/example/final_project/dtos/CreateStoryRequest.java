package org.example.final_project.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateStoryRequest {
    @NotBlank(message = "Image is required")
    @Size(max = 2_048, message = "Image URL must not exceed 2048 characters")
    private String image;

    @Size(max = 2_200, message = "Caption must not exceed 2200 characters")
    private String captions;

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getCaptions() { return captions; }
    public void setCaptions(String captions) { this.captions = captions; }
}
