package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.ApiResponse;
import com.tofutracker.Coremods.dto.igdb.SearchGameByNameResponse;
import com.tofutracker.Coremods.services.IgdbApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Slf4j
public class GameController {
    
    private final IgdbApiService igdbApiService;
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchGameByNameResponse>>> searchGames(
            @RequestParam("q") String query) {
        
        log.info("Received game search request for query: {}", query);
        
        try {
            List<SearchGameByNameResponse> games = igdbApiService.searchGames(query);
            return ResponseEntity.ok(ApiResponse.success("Games found", games));
        } catch (Exception e) {
            log.error("Error searching for games", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to search games"));
        }
    }
} 