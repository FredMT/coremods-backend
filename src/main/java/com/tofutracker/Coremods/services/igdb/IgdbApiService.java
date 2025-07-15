package com.tofutracker.Coremods.services.igdb;

import com.tofutracker.Coremods.dto.igdb.GameSummaryResponse;
import com.tofutracker.Coremods.dto.igdb.SearchGameByNameResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IgdbApiService {
    
    private final RestTemplate restTemplate;
    private final IgdbAuthService igdbAuthService;
    private final IgdbCacheService igdbCacheService;
    
    @Value("${igdb.api.base-url}")
    private String igdbBaseUrl;
    
    @RateLimiter(name = "igdb")
    public List<GameSummaryResponse> searchGames(String query) {
        log.info("Searching games with query: {}", query);
        
        String requestBody = buildGameSearchQuery(query);
        HttpEntity<String> entity = igdbAuthService.createIgdbHttpEntity(requestBody);
        
        try {
            ResponseEntity<SearchGameByNameResponse[]> response = restTemplate.exchange(
                    igdbBaseUrl + "/games",
                    HttpMethod.POST,
                    entity,
                    SearchGameByNameResponse[].class);
            
            SearchGameByNameResponse[] games = response.getBody();
            if (games == null) {
                return Collections.emptyList();
            }
            
            List<SearchGameByNameResponse> gamesList = List.of(games);
            log.info("Found {} games for query: {}", gamesList.size(), query);
            
            igdbCacheService.cacheGames(gamesList);
            
            // Return only the simplified data to the frontend
            return gamesList.stream()
                    .map(SearchGameByNameResponse::toGameSummary)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to search games for query: {}", query, e);
            return Collections.emptyList();
        }
    }
    
    private String buildGameSearchQuery(String query) {
        return String.format("search \"%s\"; fields id,name,summary,cover.*,release_dates.*,platforms.*; limit 10;", query);
    }
} 