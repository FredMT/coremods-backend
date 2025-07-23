package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.DLCResponse;
import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.services.mods.DLCSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games/{gameId}/dlcs")
@RequiredArgsConstructor
public class DLCSearchController {
    private final DLCSearchService dlcSearchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DLCResponse>>> getGameDLCs(
            @PathVariable("gameId") IgdbGame game) {
        return dlcSearchService.getDLCsForGameByGameId(game);
    }
}
