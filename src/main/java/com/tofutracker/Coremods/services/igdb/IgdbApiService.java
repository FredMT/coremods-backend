package com.tofutracker.Coremods.services.igdb;

import com.tofutracker.Coremods.config.enums.IGDBEndpoint;
import com.tofutracker.Coremods.dto.igdb.GameSummaryResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IgdbApiService {

    private final RestTemplate restTemplate;
    private final IgdbAuthService igdbAuthService;
    private final IgdbCacheService igdbCacheService;

    @RateLimiter(name = "igdb")
    public List<GameSummaryResponse> searchGames(String query) {

        String frontendRequestBody = buildFrontendSearchQuery(query);
        HttpEntity<String> frontendEntity = igdbAuthService.createIgdbHttpEntity(frontendRequestBody);

        try {
            ResponseEntity<GameSummaryResponse[]> frontendResponse = restTemplate.exchange(
                    IGDBEndpoint.GAMES,
                    HttpMethod.POST,
                    frontendEntity,
                    GameSummaryResponse[].class);

            GameSummaryResponse[] frontendGames = frontendResponse.getBody();
            List<GameSummaryResponse> gameSummaries = frontendGames != null ? List.of(frontendGames)
                    : Collections.emptyList();

            igdbCacheService.triggerBackgroundCaching(query);

            return gameSummaries;
        } catch (Exception e) {
            log.error("Failed to search games for query: {}", query, e);
            return Collections.emptyList();
        }
    }

    private String buildFrontendSearchQuery(String query) {
        return String.format(
                "search \"%s\"; " +
                        "fields id, cover.image_id, name; " +
//                        "where first_release_date != null & total_rating != null; " +
                        "limit 20;",
                query);
    }
}