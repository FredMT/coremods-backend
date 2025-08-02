package com.tofutracker.Coremods.dto.igdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Simplified response containing essential game information.")
public record GameSummaryResponse(
        @Schema(description = "Unique ID of the game", example = "12345")
        Long id,

        @Schema(description = "Name of the game", example = "The Legend of Zelda")
        String name,
        @JsonProperty("cover")
        @Schema(description = "Optional cover image details", nullable = true)
        Cover cover
) {
    @Schema(description = "Cover image information")
    public record Cover(
            @JsonProperty("image_id")
            @Schema(description = "ID of the cover image", example = "abc123")
            String imageId
    ) {}
}