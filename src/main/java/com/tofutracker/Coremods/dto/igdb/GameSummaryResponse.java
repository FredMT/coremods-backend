package com.tofutracker.Coremods.dto.igdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified response DTO containing only essential game information
 * for frontend display purposes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSummaryResponse {
    private Long id;
    private String name;

    @JsonProperty("cover")
    private Cover cover;

    public GameSummaryResponse(Long id, String name, String imageId) {
        this.id = id;
        this.name = name;
        this.cover = imageId != null ? new Cover(imageId) : null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cover {
        @JsonProperty("image_id")
        private String imageId;
    }
}