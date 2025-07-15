package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.GameCategoryResponse;
import com.tofutracker.Coremods.dto.requests.ModDetailsRequest;
import com.tofutracker.Coremods.entity.GameModCategory;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModCategoryRepository;
import com.tofutracker.Coremods.repository.IgdbGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModUploadService {

    private final GameModCategoryRepository gameModCategoryRepository;
    private final IgdbGameRepository igdbGameRepository;
    private final ModDetailsService modDetailsService;

    @Transactional(readOnly = true)
    public ApiResponse<List<GameCategoryResponse>> getGameCategories(Long gameId) {
        if (!igdbGameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game not found with ID: " + gameId);
        }

        var game = igdbGameRepository.getReferenceById(gameId);
        List<GameModCategory> rootCategories = gameModCategoryRepository.findRootCategoriesWithChildrenByGame(game);
        
        List<GameCategoryResponse> categoryResponses = rootCategories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        categoryResponses.sort((a, b) -> {
            if ("Miscellaneous".equals(a.getName())) return -1;
            if ("Miscellaneous".equals(b.getName())) return 1;
            return a.getName().compareTo(b.getName());
        });

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
} 