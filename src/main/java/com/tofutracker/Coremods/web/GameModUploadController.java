package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.upload_mod.GameCategoryResponse;
import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModDetailsRequest;
import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModRequirementsMirrorsRequest;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModUploadService;
import com.tofutracker.Coremods.services.mods.ModRequirementsMirrorsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
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
    private final ModRequirementsMirrorsService modRequirementsMirrorsService;

    @GetMapping("/game/{gameId}/categories")
    public ResponseEntity<ApiResponse<List<GameCategoryResponse>>> getGameCategories(@PathVariable Long gameId) {
        ApiResponse<List<GameCategoryResponse>> response = modUploadService.getGameCategories(gameId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/upload/details")
    public ResponseEntity<ApiResponse<Void>> saveModDetails(
            @Valid @RequestBody ModDetailsRequest modDetailsRequest,
            @AuthenticationPrincipal User currentUser) {

        log.info("Saving mod details for user: {}, mod: {}",
                currentUser.getUsername(), modDetailsRequest.getName());

        ApiResponse<Void> response = modUploadService.saveModDetails(modDetailsRequest, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{modId}/upload/requirements-mirrors")
    public ResponseEntity<ApiResponse<Void>> saveModRequirementsMirrors(
            @Valid @RequestBody ModRequirementsMirrorsRequest request,
            @PathVariable("modId") GameMod gameMod,
            @AuthenticationPrincipal User currentUser) {

        ApiResponse<Void> response = modRequirementsMirrorsService.saveModRequirementsMirrors(request, gameMod,
                currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}