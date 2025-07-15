package com.tofutracker.Coremods.dto.requests;

import com.tofutracker.Coremods.dto.annotation.ValueOfEnum;
import com.tofutracker.Coremods.entity.GameMod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModDetailsRequest {
    
    @NotNull(message = "Game ID is required")
    private Long gameId;
    
    @NotBlank(message = "Mod type is required")
    @ValueOfEnum(enumClass = GameMod.ModType.class, message = "Mod type must be MOD or TRANSLATION")
    private String modType;
    
    /**
     * Category ID for the mod. This represents the selected category which can be:
     * - A main/root category (e.g., "Gameplay", "Graphics & Visuals") 
     * - A subcategory under a main category (e.g., "Textures" under "Graphics & Visuals")
     * 
     * When suggestedCategory is provided, this should be the main category ID
     * under which the new subcategory will be created.
     */
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    /**
     * Name for a new subcategory to be created under the main category specified by categoryId.
     * This is only used when the user wants to create a custom subcategory.
     * The system will validate that this name doesn't already exist as a subcategory 
     * under the specified main category.
     * 
     * If this field is provided:
     * - categoryId must reference a main/root category
     * - A new subcategory with this name will be created under that main category
     * - The new subcategory will be used for the mod
     */
    private String suggestedCategory;
    
    @NotBlank(message = "Mod name is required")
    private String name;
    
    private String language;
    
    @NotBlank(message = "Version is required")
    private String version;
    
    @NotBlank(message = "Author is required")
    private String author;

    @Size(min = 1, max = 350, message = "Overview must not exceed 350 characters")
    private String overview;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    // Adult content flags
    private boolean hasNudity;
    private boolean hasSkimpyOutfits;
    private boolean hasExtremeViolence;
    private boolean isSexualized;
    private boolean hasProfanity;
    
    // Classification flags
    private boolean isCharacterPreset;
    private boolean hasRealWorldReferences;
    private boolean includesVisualPreset;
    private boolean hasSaveFiles;
    private boolean hasTranslationFiles;
} 