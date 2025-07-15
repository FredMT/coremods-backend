package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.requests.ModDetailsRequest;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.GameModCategory;
import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModCategoryRepository;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.IgdbGameRepository;
import com.tofutracker.Coremods.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModDetailsService {

    private final GameModRepository gameModRepository;
    private final IgdbGameRepository igdbGameRepository;
    private final UserRepository userRepository;
    private final GameModCategoryRepository gameModCategoryRepository;

    @Transactional
    public void saveModDraft(ModDetailsRequest modDetailsRequest, User currentUser) {
        IgdbGame game = igdbGameRepository.findById(modDetailsRequest.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + modDetailsRequest.getGameId()));

        if (gameModRepository.existsByNameAndGame(modDetailsRequest.getName(), game)) {
            throw new BadRequestException("A mod with the same name already exists for this game");
        }

        User author = userRepository.findByUsername(modDetailsRequest.getAuthor())
                .orElseThrow(() -> new BadRequestException("Author not found with username: " + modDetailsRequest.getAuthor()));

        GameModCategory category = resolveCategory(modDetailsRequest, game);

        GameMod gameMod = GameMod.builder()
                .game(game)
                .modType(GameMod.ModType.valueOf(modDetailsRequest.getModType()))
                .category(category)
                .suggestedCategory(modDetailsRequest.getSuggestedCategory())
                .name(modDetailsRequest.getName())
                .language(modDetailsRequest.getLanguage())
                .version(modDetailsRequest.getVersion())
                .author(author)
                .overview(modDetailsRequest.getOverview())
                .description(modDetailsRequest.getDescription())
                .hasNudity(modDetailsRequest.isHasNudity())
                .hasSkimpyOutfits(modDetailsRequest.isHasSkimpyOutfits())
                .hasExtremeViolence(modDetailsRequest.isHasExtremeViolence())
                .isSexualized(modDetailsRequest.isSexualized())
                .hasProfanity(modDetailsRequest.isHasProfanity())
                .isCharacterPreset(modDetailsRequest.isCharacterPreset())
                .hasRealWorldReferences(modDetailsRequest.isHasRealWorldReferences())
                .includesVisualPreset(modDetailsRequest.isIncludesVisualPreset())
                .hasSaveFiles(modDetailsRequest.isHasSaveFiles())
                .hasTranslationFiles(modDetailsRequest.isHasTranslationFiles())
                .status(GameMod.ModStatus.DRAFT)
                .build();

        gameModRepository.save(gameMod);
    }

    private GameModCategory resolveCategory(ModDetailsRequest modDetailsRequest, IgdbGame game) {
        GameModCategory selectedCategory = validateAndGetCategory(modDetailsRequest.getCategoryId(), game);
        
        if (modDetailsRequest.getSuggestedCategory() != null && !modDetailsRequest.getSuggestedCategory().trim().isEmpty()) {
            return createSubcategory(selectedCategory, modDetailsRequest.getSuggestedCategory().trim(), game);
        } else {
            return selectedCategory;
        }
    }
    
    private GameModCategory createSubcategory(GameModCategory mainCategory, String subcategoryName, IgdbGame game) {
        if (!mainCategory.isRootCategory()) {
            throw new BadRequestException("Cannot create subcategory under a subcategory. Please select a main category.");
        }
        
        validateSubcategoryName(subcategoryName);
        
        if (subcategoryAlreadyExists(mainCategory, subcategoryName, game)) {
            throw new BadRequestException("A subcategory with the name '" + subcategoryName + "' already exists under '" + mainCategory.getCategoryName() + "'");
        }
        
        GameModCategory newSubcategory = new GameModCategory();
        newSubcategory.setGame(game);
        newSubcategory.setCategoryName(subcategoryName);
        newSubcategory.setParent(mainCategory);
        newSubcategory.setApproved(false);
        
        return gameModCategoryRepository.save(newSubcategory);
    }
    
    private boolean subcategoryAlreadyExists(GameModCategory mainCategory, String subcategoryName, IgdbGame game) {
        Optional<GameModCategory> existingSubcategory = gameModCategoryRepository
                .findByGameAndCategoryNameIgnoreCaseAndParent(game, subcategoryName, mainCategory);
        return existingSubcategory.isPresent();
    }

    private GameModCategory validateAndGetCategory(Long categoryId, IgdbGame game) {
        GameModCategory category = gameModCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        
        if (!category.getGame().getId().equals(game.getId())) {
            throw new BadRequestException("Category does not belong to the specified game");
        }
        
        return category;
    }

    private void validateSubcategoryName(String subcategoryName) {
        if (subcategoryName == null || subcategoryName.trim().isEmpty()) {
            throw new BadRequestException("Subcategory name cannot be empty");
        }
        
        if (subcategoryName.length() > 100) {
            throw new BadRequestException("Subcategory name cannot exceed 100 characters");
        }
        
        if (subcategoryName.matches(".*[<>\"&].*")) {
            throw new BadRequestException("Subcategory name contains invalid characters");
        }
    }
} 