package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.responses.mods.DLCResponse;
import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.IgdbGameDLCRepository;
import com.tofutracker.Coremods.repository.IgdbGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DLCSearchService {

    private final IgdbGameDLCRepository igdbGameDLCRepository;
    private final IgdbGameRepository igdbGameRepository;

    public List<DLCResponse> getDLCsByGameId(Long gameId) {
        IgdbGame game = igdbGameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));

        List<IgdbGame> dlcGames = igdbGameDLCRepository.findAllDLCGamesByParentGameId(game.getId());

        return dlcGames.stream()
                .map(dlcGame -> DLCResponse.builder()
                        .id(dlcGame.getId())
                        .name(dlcGame.getName())
                        .build())
                .toList();
    }
}
