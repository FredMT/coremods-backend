package com.tofutracker.Coremods.dto.requests.mods.media;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class YouTubeVideoUpdateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
} 