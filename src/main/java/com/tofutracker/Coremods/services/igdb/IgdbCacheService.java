package com.tofutracker.Coremods.services.igdb;

import com.tofutracker.Coremods.dto.igdb.SearchGameByNameResponse;
import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.entity.IgdbPlatform;
import com.tofutracker.Coremods.repository.IgdbGameRepository;
import com.tofutracker.Coremods.repository.IgdbPlatformRepository;
import com.tofutracker.Coremods.services.mods.PresetCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IgdbCacheService {

    private final IgdbGameRepository gameRepository;
    private final IgdbPlatformRepository platformRepository;
    private final PresetCategoryService presetCategoryService;

    /**
     * Asynchronously caches game data from IGDB API responses
     * This runs in the background and doesn't block the API response
     */
    @Async
    @Transactional
    public void cacheGames(List<SearchGameByNameResponse> games) {
        if (games == null || games.isEmpty()) {
            return;
        }

        log.info("Caching {} games from IGDB API", games.size());
        
        for (SearchGameByNameResponse gameResponse : games) {
            try {
                cacheGame(gameResponse);
            } catch (Exception e) {
                log.error("Error caching game: {}", gameResponse.getName(), e);
            }
        }
    }

    @Transactional
    protected void cacheGame(SearchGameByNameResponse gameResponse) {

        if (gameRepository.existsById(gameResponse.getId())) {
            return;
        }

        IgdbGame game = new IgdbGame();
        game.setId(gameResponse.getId());
        game.setName(gameResponse.getName());
        game.setSummary(gameResponse.getSummary());
        
        if (gameResponse.getCover() != null) {
            game.setCoverUrl(gameResponse.getCover().getImageId());
        }
        
        game.setReleaseDate(gameResponse.getEarliestReleaseDate());

        if (gameResponse.getPlatforms() != null) {
            for (SearchGameByNameResponse.Platform platformResponse : gameResponse.getPlatforms()) {
                IgdbPlatform platform = getOrCreatePlatform(platformResponse);
                game.addPlatform(platform);
            }
        }

        gameRepository.save(game);
        
        // Create all preset categories for the game
        if (!presetCategoryService.presetCategoriesExist(game)) {
            presetCategoryService.createPresetCategoriesForGame(game);
        }
        
        log.info("Cached game: {}", game.getName());
    }

    private IgdbPlatform getOrCreatePlatform(SearchGameByNameResponse.Platform platformResponse) {
        Optional<IgdbPlatform> existingPlatform = platformRepository.findById(platformResponse.getId());
        
        if (existingPlatform.isPresent()) {
            return existingPlatform.get();
        }
        
        IgdbPlatform platform = new IgdbPlatform();
        platform.setId(platformResponse.getId());
        platform.setName(platformResponse.getName());
        
        return platformRepository.save(platform);
    }
} 