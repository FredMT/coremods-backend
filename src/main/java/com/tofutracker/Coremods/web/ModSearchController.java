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

    // TODO: Update it later to search only for published mods

    private final ModSearchService modSearchService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ModSearchResponse>>> searchMods(@RequestParam("q") String searchQuery) {
        return modSearchService.searchMods(searchQuery);
    }
}
