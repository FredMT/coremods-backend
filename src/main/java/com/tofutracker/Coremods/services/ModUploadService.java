package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.dto.ApiResponse;
import com.tofutracker.Coremods.dto.GameCategoryResponse;
import com.tofutracker.Coremods.entity.GameModCategory;
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

    /**
     * Get all categories for a specific game
     * 
     * @param gameId The ID of the game
     * @return List of categories with their IDs
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<GameCategoryResponse>> getGameCategories(Long gameId) {

        if (!igdbGameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game not found with ID: " + gameId);
        }

        List<GameModCategory> categories = gameModCategoryRepository.findByGameId(gameId);
        
        List<GameCategoryResponse> categoryResponses = categories.stream()
                .map(category -> new GameCategoryResponse(
                        category.getId(),
                        category.getCategoryName(),
                        category.isApproved()))
                .collect(Collectors.toList());

        return ApiResponse.success("Categories retrieved successfully", categoryResponses);
    }
} 