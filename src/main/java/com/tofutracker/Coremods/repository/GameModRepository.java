package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.IgdbGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameModRepository extends JpaRepository<GameMod, Long> {
    /**
     * Checks if a mod with the given name exists for the specified game
     * 
     * @param name The name of the mod
     * @param game The game the mod is for
     * @return true if a mod with the name exists for the game, false otherwise
     */
    boolean existsByNameAndGame(String name, IgdbGame game);
}