package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.responses.mods.ModSearchResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.repository.GameModRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModSearchService {

    private final GameModRepository gameModRepository;

    public List<ModSearchResponse> searchMods(String searchQuery) {
        List<GameMod> gameMods = gameModRepository.searchGameModByNameAndIsPublished(searchQuery);
        return gameMods.stream()
                .map(ModSearchResponse::fromGameMod)
                .toList();
    }
}
