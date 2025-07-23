package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.DLCResponse;
import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.IgdbGameDLCRepository;
import com.tofutracker.Coremods.repository.IgdbGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DLCSearchService {

    private final IgdbGameDLCRepository igdbGameDLCRepository;
    private final IgdbGameRepository igdbGameRepository;

    public ResponseEntity<ApiResponse<List<DLCResponse>>> getDLCsForGameByGameId(IgdbGame game) {
        List<IgdbGame> dlcGames = igdbGameDLCRepository.findAllDLCGamesByParentGameId(game.getId());

        List<DLCResponse> dlcResponses = dlcGames.stream()
                .map(dlcGame -> DLCResponse.builder()
                        .id(dlcGame.getId())
                        .name(dlcGame.getName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("DLCs retrieved successfully", dlcResponses));
    }

    public ResponseEntity<ApiResponse<List<DLCResponse>>> getDLCsForGameByGameId(Long gameId) {
        IgdbGame game = igdbGameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));

        return getDLCsForGameByGameId(game);
    }
}
