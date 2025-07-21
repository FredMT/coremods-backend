package com.tofutracker.Coremods.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IgdbGamePlatformId implements Serializable {
    private Long gameId;
    private Long platformId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IgdbGamePlatformId that = (IgdbGamePlatformId) o;
        return Objects.equals(gameId, that.gameId) && Objects.equals(platformId, that.platformId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, platformId);
    }
}