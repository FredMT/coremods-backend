package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.igdb.GameSummaryResponse;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.services.igdb.IgdbApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        List<GameSummaryResponse> games = igdbApiService.searchGames(query);

        if (games.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No games found", Collections.emptyList()));
        }

        return ResponseEntity.ok(ApiResponse.success("Games found", games));
    }
}