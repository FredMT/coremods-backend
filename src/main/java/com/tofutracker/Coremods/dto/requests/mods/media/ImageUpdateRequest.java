package com.tofutracker.Coremods.dto.requests.mods.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUpdateRequest {
    
    @NotBlank(message = "Image name is required")
    @Size(min = 1, max = 255, message = "Image name must be between 1 and 255 characters")
    private String name;
} 