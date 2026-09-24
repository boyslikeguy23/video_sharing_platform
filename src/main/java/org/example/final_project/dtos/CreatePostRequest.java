package org.example.final_project.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePostRequest {
    @Size(max = 2_200, message = "Caption must not exceed 2200 characters")
    private String caption;

    @NotBlank(message = "Image is required")
    @Size(max = 2_048, message = "Image URL must not exceed 2048 characters")
    private String image;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
