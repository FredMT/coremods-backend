package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.GameCategoryResponse;
import com.tofutracker.Coremods.dto.requests.ModDetailsRequest;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mods")
@RequiredArgsConstructor
@Slf4j
public class GameModUploadController {

    private final ModUploadService modUploadService;

    @GetMapping("/game/{gameId}/categories")
    public ResponseEntity<ApiResponse<List<GameCategoryResponse>>> getGameCategories(@PathVariable Long gameId) {
        ApiResponse<List<GameCategoryResponse>> response = modUploadService.getGameCategories(gameId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/upload/details")
    public ResponseEntity<ApiResponse<Void>> saveModDetails(
            @Valid @RequestBody ModDetailsRequest modDetailsRequest,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Saving mod details for user: {}, mod: {}", 
                currentUser.getUsername(), modDetailsRequest.getName());
        
        ApiResponse<Void> response = modUploadService.saveModDetails(modDetailsRequest, currentUser);
        return ResponseEntity.ok(response);
    }
} 