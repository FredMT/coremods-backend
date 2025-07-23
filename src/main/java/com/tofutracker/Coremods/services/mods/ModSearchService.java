package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.ModSearchResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.repository.GameModRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModSearchService {

    private final GameModRepository gameModRepository;

    public ResponseEntity<ApiResponse<List<ModSearchResponse>>> searchMods(String searchQuery) {
        List<GameMod> gameMods = gameModRepository.searchGameModByName(searchQuery);
        List<ModSearchResponse> modResponses = gameMods.stream()
                .map(ModSearchResponse::fromGameMod)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Mods retrieved successfully", modResponses));
    }
}
