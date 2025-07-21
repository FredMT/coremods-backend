package com.tofutracker.Coremods.services.igdb;

import com.tofutracker.Coremods.config.enums.IGDBEndpoint;
import com.tofutracker.Coremods.dto.igdb.SearchGameByNameResponse;
import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.entity.IgdbGameDLC;
import com.tofutracker.Coremods.entity.IgdbGamePlatform;
import com.tofutracker.Coremods.entity.IgdbPlatform;
import com.tofutracker.Coremods.repository.IgdbGameDLCRepository;
import com.tofutracker.Coremods.repository.IgdbGamePlatformRepository;
import com.tofutracker.Coremods.repository.IgdbGameRepository;
import com.tofutracker.Coremods.repository.IgdbPlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IgdbCacheService {

    private final IgdbGameRepository gameRepository;
    private final IgdbPlatformRepository platformRepository;
    private final IgdbGameDLCRepository igdbGameDLCRepository;
    private final IgdbGamePlatformRepository igdbGamePlatformRepository;
    private final RestTemplate restTemplate;
    private final IgdbAuthService igdbAuthService;

    /**
     * Triggers the background caching process for a given search query
     * This method starts an asynchronous process to fetch and cache detailed game
     * data
     */
    @Async
    public void triggerBackgroundCaching(String query) {
        log.info("Triggering background caching for query: {}", query);
        try {
            String cachingRequestBody = buildCachingSearchQuery(query);
            HttpEntity<String> cachingEntity = igdbAuthService.createIgdbHttpEntity(cachingRequestBody);

            ResponseEntity<SearchGameByNameResponse[]> cachingResponse = restTemplate.exchange(
                    IGDBEndpoint.GAMES,
                    HttpMethod.POST,
                    cachingEntity,
                    SearchGameByNameResponse[].class);

            SearchGameByNameResponse[] cachingGames = cachingResponse.getBody();
            if (cachingGames != null && cachingGames.length > 0) {
                List<SearchGameByNameResponse> cachingGamesList = List.of(cachingGames);
                log.info("Found {} games for background caching", cachingGamesList.size());

                cacheGamesAsync(cachingGamesList);
            }
        } catch (Exception e) {
            log.error("Failed to trigger background caching for query: {}", query, e);
        }
    }

    private String buildCachingSearchQuery(String query) {
        return String.format(
                "search \"%s\"; " +
                        "fields involved_companies.company.published.id," +
                        "involved_companies.company.published.name," +
                        "involved_companies.company.published.slug," +
                        "involved_companies.company.published.updated_at," +
                        "involved_companies.company.published.cover.image_id," +
                        "involved_companies.company.published.platforms.id," +
                        "involved_companies.company.published.platforms.name," +
                        "involved_companies.company.published.dlcs.id," +
                        "involved_companies.company.published.dlcs.name," +
                        "involved_companies.company.published.dlcs.slug," +
                        "involved_companies.company.published.dlcs.updated_at," +
                        "involved_companies.company.published.dlcs.cover.id," +
                        "involved_companies.company.published.dlcs.cover.image_id," +
                        "involved_companies.company.published.dlcs.platforms.id," +
                        "involved_companies.company.published.dlcs.platforms.name; " +
                        "where first_release_date != null & total_rating != null; " +
                        "limit 30;",
                query);
    }

    @Async
    public void cacheGamesAsync(List<SearchGameByNameResponse> searchResults) {
        log.info("Starting async caching for {} search results", searchResults != null ? searchResults.size() : 0);
        try {
            cacheGames(searchResults);
        } catch (Exception e) {
            log.error("Error during async game caching", e);
        }
    }

    public void cacheGames(List<SearchGameByNameResponse> searchResults) {
        if (searchResults == null || searchResults.isEmpty()) {
            return;
        }

        log.info("Processing {} search results from IGDB API", searchResults.size());

        Set<Long> seenGameIds = new HashSet<>();
        Set<Long> seenPlatformIds = new HashSet<>();
        Set<Long> seenCompanyIds = new HashSet<>();
        Set<String> seenDlcRelationships = new HashSet<>();
        Set<Long> dlcIds = new HashSet<>();

        List<IgdbGame> allGames = new ArrayList<>();
        List<IgdbPlatform> allPlatforms = new ArrayList<>();
        List<IgdbGameDLC> allDlcRelationships = new ArrayList<>();
        List<IgdbGamePlatform> allGamePlatforms = new ArrayList<>();

        for (SearchGameByNameResponse searchResult : searchResults) {
            if (searchResult.getInvolvedCompanies() == null) {
                continue;
            }

            for (SearchGameByNameResponse.InvolvedCompany involvedCompany : searchResult.getInvolvedCompanies()) {
                if (involvedCompany.getCompany() == null || involvedCompany.getCompany().getPublished() == null) {
                    continue;
                }

                Long companyId = involvedCompany.getCompany().getId();
                if (seenCompanyIds.contains(companyId)) {
                    continue;
                }
                seenCompanyIds.add(companyId);

                for (SearchGameByNameResponse.PublishedGame publishedGame : involvedCompany.getCompany()
                        .getPublished()) {
                    processPublishedGame(publishedGame, seenGameIds, seenPlatformIds, seenDlcRelationships,
                            allGames, allPlatforms, allDlcRelationships, allGamePlatforms, dlcIds);
                }
            }
        }

        batchSaveEntities(allGames, allPlatforms, allDlcRelationships, allGamePlatforms);

        log.info("Cached {} games, {} platforms, {} DLC relationships from involved companies expansion",
                allGames.size(), allPlatforms.size(), allDlcRelationships.size());

    }

    private void processPublishedGame(SearchGameByNameResponse.PublishedGame publishedGame,
            Set<Long> seenGameIds, Set<Long> seenPlatformIds,
            Set<String> seenDlcRelationships,
            List<IgdbGame> allGames, List<IgdbPlatform> allPlatforms,
            List<IgdbGameDLC> allDlcRelationships,
            List<IgdbGamePlatform> allGamePlatforms, Set<Long> dlcIds) {

        if (publishedGame.getId() == null || seenGameIds.contains(publishedGame.getId())) {
            return;
        }
        seenGameIds.add(publishedGame.getId());

        IgdbGame game = IgdbGame.builder()
                .id(publishedGame.getId())
                .name(publishedGame.getName())
                .slug(publishedGame.getSlug())
                .updatedAtIgdb(publishedGame.getUpdatedAt())
                .coverImageId(publishedGame.getCover() != null ? publishedGame.getCover().getImageId() : null)
                .build();
        allGames.add(game);

        if (publishedGame.getPlatforms() != null) {
            for (SearchGameByNameResponse.Platform platformResponse : publishedGame.getPlatforms()) {
                if (platformResponse.getId() != null && !seenPlatformIds.contains(platformResponse.getId())) {
                    IgdbPlatform platform = new IgdbPlatform();
                    platform.setId(platformResponse.getId());
                    platform.setName(platformResponse.getName());
                    allPlatforms.add(platform);
                    seenPlatformIds.add(platformResponse.getId());
                }

                if (platformResponse.getId() != null) {
                    IgdbGamePlatform gamePlatform = IgdbGamePlatform.builder()
                            .gameId(publishedGame.getId())
                            .platformId(platformResponse.getId())
                            .build();
                    allGamePlatforms.add(gamePlatform);
                }
            }
        }

        // Process DLCs
        if (publishedGame.getDlcs() != null) {
            for (SearchGameByNameResponse.DLC dlc : publishedGame.getDlcs()) {
                // Mark this ID as a DLC
                if (dlc.getId() != null) {
                    dlcIds.add(dlc.getId());
                }

                // Process DLC as a regular game (to create game->dlc relationship) and so that DLCs can also have their own mods
                if (dlc.getId() != null && !seenGameIds.contains(dlc.getId())) {
                    seenGameIds.add(dlc.getId());

                    IgdbGame dlcGame = IgdbGame.builder()
                            .id(dlc.getId())
                            .name(dlc.getName())
                            .slug(dlc.getSlug())
                            .updatedAtIgdb(dlc.getUpdatedAt())
                            .coverImageId(dlc.getCover() != null ? dlc.getCover().getImageId() : null)
                            .build();
                    allGames.add(dlcGame);

                    // Process DLC platforms
                    if (dlc.getPlatforms() != null) {
                        for (SearchGameByNameResponse.Platform platformResponse : dlc.getPlatforms()) {
                            if (platformResponse.getId() != null
                                    && !seenPlatformIds.contains(platformResponse.getId())) {
                                IgdbPlatform platform = new IgdbPlatform();
                                platform.setId(platformResponse.getId());
                                platform.setName(platformResponse.getName());
                                allPlatforms.add(platform);
                                seenPlatformIds.add(platformResponse.getId());
                            }

                            // Create DLC-platform relationship
                            if (platformResponse.getId() != null) {
                                IgdbGamePlatform dlcPlatform = IgdbGamePlatform.builder()
                                        .gameId(dlc.getId())
                                        .platformId(platformResponse.getId())
                                        .build();
                                allGamePlatforms.add(dlcPlatform);
                            }
                        }
                    }
                }

                // Create DLC relationship
                String relationshipKey = publishedGame.getId() + "-" + dlc.getId();
                if (!seenDlcRelationships.contains(relationshipKey)) {
                    IgdbGameDLC gameDLC = IgdbGameDLC.builder()
                            .parentGame(game)
                            .dlcId(dlc.getId())
                            .build();
                    allDlcRelationships.add(gameDLC);
                    seenDlcRelationships.add(relationshipKey);
                }
            }
        }
    }

    private void batchSaveEntities(List<IgdbGame> allGames, List<IgdbPlatform> allPlatforms,
            List<IgdbGameDLC> allDlcRelationships, List<IgdbGamePlatform> allGamePlatforms) {

        // Get existing IDs to avoid duplicates
        Set<Long> existingGameIds = gameRepository.findAllById(
                allGames.stream().map(IgdbGame::getId).collect(Collectors.toList())).stream().map(IgdbGame::getId)
                .collect(Collectors.toSet());

        Set<Long> existingPlatformIds = platformRepository.findAllById(
                allPlatforms.stream().map(IgdbPlatform::getId).collect(Collectors.toList())).stream()
                .map(IgdbPlatform::getId).collect(Collectors.toSet());

        // Save only new games
        List<IgdbGame> newGames = allGames.stream()
                .filter(game -> !existingGameIds.contains(game.getId()))
                .collect(Collectors.toList());
        if (!newGames.isEmpty()) {
            gameRepository.saveAll(newGames);
        }

        // Save only new platforms
        List<IgdbPlatform> newPlatforms = allPlatforms.stream()
                .filter(platform -> !existingPlatformIds.contains(platform.getId()))
                .collect(Collectors.toList());
        if (!newPlatforms.isEmpty()) {
            platformRepository.saveAll(newPlatforms);
        }

        // Save game-platform relationships (checking for existing relationships)
        List<IgdbGamePlatform> newGamePlatforms = allGamePlatforms.stream()
                .filter(gp -> !igdbGamePlatformRepository.existsByGameIdAndPlatformId(gp.getGameId(),
                        gp.getPlatformId()))
                .collect(Collectors.toList());
        if (!newGamePlatforms.isEmpty()) {
            igdbGamePlatformRepository.saveAll(newGamePlatforms);
        }

        // Save DLC relationships (checking for existing relationships)
        List<IgdbGameDLC> newDlcRelationships = allDlcRelationships.stream()
                .filter(dlc -> !igdbGameDLCRepository.existsByParentGameIdAndDlcId(
                        dlc.getParentGame().getId(), dlc.getDlcId()))
                .collect(Collectors.toList());
        if (!newDlcRelationships.isEmpty()) {
            igdbGameDLCRepository.saveAll(newDlcRelationships);
        }
    }
}