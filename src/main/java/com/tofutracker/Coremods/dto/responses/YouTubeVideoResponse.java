package com.tofutracker.Coremods.dto.responses;

import com.tofutracker.Coremods.entity.YouTubeVideo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeVideoResponse {
    private Long id;
    private String youtubeUrl;
    private String title;
    private String description;
    private Integer displayOrder;
    private LocalDateTime createdTime;
    
    public static YouTubeVideoResponse from(YouTubeVideo video) {
        return new YouTubeVideoResponse(
                video.getId(),
                video.getYoutubeUrl(),
                video.getTitle(),
                video.getDescription(),
                video.getDisplayOrder(),
                video.getCreatedTime()
        );
    }
    
    public static List<YouTubeVideoResponse> from(List<YouTubeVideo> videos) {
        return videos.stream()
                .map(YouTubeVideoResponse::from)
                .collect(Collectors.toList());
    }
} 