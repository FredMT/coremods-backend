package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModDetailsRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.upload_mod.GameCategoryResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.GameModCategory;
import com.tofutracker.Coremods.entity.ModPermissions;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModUploadService {

    private final GameModCategoryRepository gameModCategoryRepository;
    private final IgdbGameRepository igdbGameRepository;
    private final ModDetailsService modDetailsService;
    private final GameModRepository gameModRepository;
    private final ModFileRepository modFileRepository;
    private final ModPermissionsRepository modPermissionsRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<GameCategoryResponse>> getGameCategories(Long gameId) {
        if (!igdbGameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game not found with ID: " + gameId);
        }

        var game = igdbGameRepository.getReferenceById(gameId);
        List<GameModCategory> rootCategories = gameModCategoryRepository.findRootCategoriesWithChildrenByGame(game);
        
        List<GameCategoryResponse> categoryResponses = rootCategories.stream()
                .map(this::convertToResponse).sorted((a, b) -> {
                    if ("Miscellaneous".equals(a.getName())) return -1;
                    if ("Miscellaneous".equals(b.getName())) return 1;
                    return a.getName().compareTo(b.getName());
                }).collect(Collectors.toList());

        return ApiResponse.success("Categories retrieved successfully", categoryResponses);
    }
    
    @Transactional
    public ApiResponse<Void> saveModDetails(ModDetailsRequest modDetailsRequest, User currentUser) {
        modDetailsService.saveModDraft(modDetailsRequest, currentUser);
        return ApiResponse.success("Mod draft details saved successfully");
    }
    
    private GameCategoryResponse convertToResponse(GameModCategory category) {
        GameCategoryResponse response = GameCategoryResponse.builder()
                .id(category.getId())
                .name(category.getCategoryName())
                .approved(category.isApproved())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getCategoryName() : null)
                .isRootCategory(category.isRootCategory())
                .hasChildren(category.hasChildren())
                .build();
        
        if (category.hasChildren()) {
            List<GameCategoryResponse> children = category.getChildren().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            response.setChildren(children);
        }
        
        return response;
    }

    public ResponseEntity<ApiResponse<Void>> publishMod(Long gameModId, User currentUser) {
        GameMod gameMod = gameModRepository.findById(gameModId).orElseThrow(
                () -> new ResourceNotFoundException("Game mod not found with ID: " + gameModId)
        );

        if (!gameMod.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to publish this mod.");
        }

        if (gameMod.getModType() == null ||
            gameMod.getCategory() == null ||
            gameMod.getName() == null || gameMod.getName().isBlank() ||
            gameMod.getVersion() == null || gameMod.getVersion().isBlank() ||
            gameMod.getDescription() == null || gameMod.getDescription().isBlank()) {
            throw new BadRequestException("Mod is missing required details (mod type, category, name, version, author, or description)");
        }

        if (modFileRepository.findByMod(gameMod).isEmpty()) {
            throw new BadRequestException("At least one file must be uploaded before publishing the mod");
        }

        Optional<ModPermissions> permissionsOpt = modPermissionsRepository.findByModIdAndIsLatestTrue(gameMod.getId());
        if (permissionsOpt.isEmpty()) {
            throw new BadRequestException("Mod permissions must be set before publishing");
        }

        gameMod.setStatus(GameMod.ModStatus.PUBLISHED);
        gameMod.setPublished(true);
        gameModRepository.save(gameMod);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Mod published successfully"));
    }
}