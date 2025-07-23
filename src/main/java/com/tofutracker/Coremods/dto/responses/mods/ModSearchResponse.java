package com.tofutracker.Coremods.dto.responses.mods;

import com.tofutracker.Coremods.entity.GameMod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModSearchResponse {
    private Long id;
    private String name;
    private String description;
    private String version;
    private Long gameId;
    private String gameName;
    private String gameCoverImageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ModSearchResponse fromGameMod(GameMod gameMod) {
        return ModSearchResponse.builder()
                .id(gameMod.getId())
                .name(gameMod.getName())
                .description(gameMod.getDescription())
                .version(gameMod.getVersion())
                .gameId(gameMod.getGame().getId())
                .gameName(gameMod.getGame().getName())
                .gameCoverImageId(gameMod.getGame().getCoverImageId())
                .createdAt(gameMod.getCreatedAt())
                .updatedAt(gameMod.getUpdatedAt())
                .build();
    }
}