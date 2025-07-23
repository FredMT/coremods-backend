package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.repository.IgdbGameRepository;
import com.tofutracker.Coremods.services.mods.PresetCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/games/categories")
@RequiredArgsConstructor
public class GameCategoryController {

    private final PresetCategoryService presetCategoryService;
    private final IgdbGameRepository igdbGameRepository;

    @PostMapping("/{gameId}/create-preset-categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createPresetCategories(@PathVariable Long gameId) {
        IgdbGame game = igdbGameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        if (presetCategoryService.presetCategoriesExist(game)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Preset categories already exist for this game"));
        }

        presetCategoryService.createPresetCategoriesForGame(game);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Preset categories created successfully"));
    }
}