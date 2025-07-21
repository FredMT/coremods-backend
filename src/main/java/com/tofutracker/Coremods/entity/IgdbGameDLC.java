package com.tofutracker.Coremods.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "igdb_game_dlcs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IgdbGameDLC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_game_id", nullable = false)
    private IgdbGame parentGame;

    @Column(name = "dlc_id", nullable = false)
    private Long dlcId;

    // Composite unique constraint to prevent duplicate DLC entries for the same
    // parent game
    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = { "parent_game_id", "dlc_id" })
    })
    public static class TableConstraints {
    }
}