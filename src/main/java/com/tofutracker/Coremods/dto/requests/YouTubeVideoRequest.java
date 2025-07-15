package com.tofutracker.Coremods.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class YouTubeVideoRequest {
    @NotBlank(message = "YouTube URL is required")
    private String youtubeUrl;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
} 