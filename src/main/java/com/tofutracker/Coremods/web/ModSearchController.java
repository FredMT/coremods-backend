package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.ModSearchResponse;
import com.tofutracker.Coremods.services.mods.ModSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mods/search")
@RequiredArgsConstructor
public class ModSearchController {

    private final ModSearchService modSearchService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ModSearchResponse>>> searchMods(@RequestParam("q") String searchQuery) {
        List<ModSearchResponse> searchResults = modSearchService.searchMods(searchQuery);

        if (searchResults.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No search results found", searchResults));
        }

        return ResponseEntity.ok(ApiResponse.success("Search results found", searchResults));
    }
}
