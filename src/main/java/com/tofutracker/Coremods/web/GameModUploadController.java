package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.ApiResponse;
import com.tofutracker.Coremods.dto.GameCategoryResponse;
import com.tofutracker.Coremods.services.ModUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mods")
@RequiredArgsConstructor
public class GameModUploadController {

    private final ModUploadService modUploadService;

    /**
     * Get all mod categories for a specific game
     * 
     * @param gameId The ID of the game
     * @return List of mod categories with their IDs and approved status
     */
    @GetMapping("/game/{gameId}/categories")
    public ResponseEntity<ApiResponse<List<GameCategoryResponse>>> getGameCategories(@PathVariable Long gameId) {
        ApiResponse<List<GameCategoryResponse>> response = modUploadService.getGameCategories(gameId);
        return ResponseEntity.ok(response);
    }
} 