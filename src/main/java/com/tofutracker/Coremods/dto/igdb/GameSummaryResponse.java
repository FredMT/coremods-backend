package com.tofutracker.Coremods.dto.igdb;

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
    private String imageId;
} 