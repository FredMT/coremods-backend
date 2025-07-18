package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.igdb.GameSummaryResponse;
import com.tofutracker.Coremods.services.igdb.IgdbApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final IgdbApiService igdbApiService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GameSummaryResponse>>> searchGames(
            @RequestParam("q") String query) {

        try {
            List<GameSummaryResponse> games = igdbApiService.searchGames(query);
            if (games.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success("No games found", Collections.emptyList()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Games found", games));
        } catch (Exception e) {
            log.error("Error searching for games", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to search games"));
        }
    }
}