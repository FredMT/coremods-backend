package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.entity.GameModCategory;
import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.repository.GameModCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresetCategoryService {

    private final GameModCategoryRepository gameModCategoryRepository;

    private static final Map<String, List<String>> PRESET_CATEGORIES = new LinkedHashMap<>();

    static {
        PRESET_CATEGORIES.put("Gameplay", Arrays.asList(
                "Overhaul", "Balance Changes", "Combat Tweaks", "Difficulty Mods",
                "Game Mechanics", "Perks & Abilities", "Level Scaling", "Survival Mechanics"));

        PRESET_CATEGORIES.put("Graphics & Visuals", Arrays.asList(
                "Textures", "Shaders", "Lighting", "ENB & ReShade Presets",
                "UI Enhancements", "HD Models", "Weather & Atmosphere", "Animations"));

        PRESET_CATEGORIES.put("Audio", Arrays.asList(
                "Sound Effects", "Music Replacements", "Voice Packs", "Ambient Sounds"));

        PRESET_CATEGORIES.put("User Interface", Arrays.asList(
                "HUD Tweaks", "Menu Replacements", "Maps & Compass", "Inventory Management"));

        PRESET_CATEGORIES.put("Weapons", Arrays.asList(
                "New Weapons", "Weapon Skins", "Weapon Balance", "Attachments"));

        PRESET_CATEGORIES.put("Armor & Clothing", Arrays.asList(
                "New Armor", "Cosmetic Outfits", "Clothing Retextures", "Armor Balance"));

        PRESET_CATEGORIES.put("Quests & Missions", Arrays.asList(
                "New Quests", "Side Missions", "Story Expansions", "Quest Fixes"));

        PRESET_CATEGORIES.put("NPCs & Characters", Arrays.asList(
                "Companions", "Followers", "NPC Behavior", "New Races or Species", "Character Appearance"));

        PRESET_CATEGORIES.put("Maps & Locations", Arrays.asList(
                "New Lands", "Dungeons", "Cities & Towns", "Player Homes", "Interiors"));

        PRESET_CATEGORIES.put("Creatures & Enemies", Arrays.asList(
                "New Enemies", "Enemy Behavior", "Bosses", "Monster Packs"));

        PRESET_CATEGORIES.put("Cheats & God Items", Arrays.asList(
                "Cheat Items", "Debug Tools", "Unlimited Resources", "God Mode Mods"));

        PRESET_CATEGORIES.put("Utilities & Tools", Arrays.asList(
                "Frameworks", "Mod Loaders", "Save Game Editors", "Performance Boosters"));

        PRESET_CATEGORIES.put("Vehicles & Transportation", Arrays.asList(
                "New Vehicles", "Vehicle Skins", "Driving Physics", "Teleportation"));

        PRESET_CATEGORIES.put("Crafting & Economy", Arrays.asList(
                "Crafting Overhauls", "Trade & Bartering", "Economy Tweaks", "Loot Systems"));

        PRESET_CATEGORIES.put("Multiplayer & Co-op", Arrays.asList(
                "Multiplayer Support", "Online Enhancements", "LAN Features"));

        PRESET_CATEGORIES.put("Bug Fixes & Patches", Arrays.asList(
                "Unofficial Patches", "Engine Fixes", "Script Fixes", "Compatibility Fixes"));

        PRESET_CATEGORIES.put("Fun", Arrays.asList(
                "Memes & Jokes", "Pop Culture References", "Crossover Content", "Uncategorized"));

        PRESET_CATEGORIES.put("NSFW", Arrays.asList(
                "Sexual Content", "Nudity", "Profanity", "Gore & Violence"));

        PRESET_CATEGORIES.put("Miscellaneous", Arrays.asList());
    }

    @Transactional
    public void createPresetCategoriesForGame(IgdbGame game) {
        List<GameModCategory> allCategories = new ArrayList<>();
        Map<String, GameModCategory> rootCategoriesMap = new HashMap<>();

        // First, create all root categories
        for (Map.Entry<String, List<String>> entry : PRESET_CATEGORIES.entrySet()) {
            String categoryName = entry.getKey();

            GameModCategory rootCategory = GameModCategory.builder()
                    .game(game)
                    .categoryName(categoryName)
                    .approved(true)
                    .build();

            allCategories.add(rootCategory);
            rootCategoriesMap.put(categoryName, rootCategory);
        }

        // Save all root categories in a batch
        List<GameModCategory> savedRootCategories = gameModCategoryRepository.saveAll(allCategories);

        // Update the map with saved entities that have IDs
        for (GameModCategory savedCategory : savedRootCategories) {
            rootCategoriesMap.put(savedCategory.getCategoryName(), savedCategory);
        }

        // Create all subcategories
        List<GameModCategory> allSubcategories = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : PRESET_CATEGORIES.entrySet()) {
            String categoryName = entry.getKey();
            List<String> subcategories = entry.getValue();
            GameModCategory rootCategory = rootCategoriesMap.get(categoryName);

            for (String subcategoryName : subcategories) {
                GameModCategory subcategory = GameModCategory.builder()
                        .game(game)
                        .categoryName(subcategoryName)
                        .parent(rootCategory)
                        .approved(true)
                        .build();

                allSubcategories.add(subcategory);
                rootCategory.addChild(subcategory);
            }
        }

        // Save all subcategories in a batch
        if (!allSubcategories.isEmpty()) {
            gameModCategoryRepository.saveAll(allSubcategories);
        }
    }

    public boolean presetCategoriesExist(IgdbGame game) {
        return gameModCategoryRepository.countByGameAndParentIsNull(game) >= PRESET_CATEGORIES.size();
    }
}