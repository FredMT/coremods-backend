package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.IgdbGame;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

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

    @Query("SELECT gm FROM GameMod gm JOIN FETCH gm.game WHERE LOWER(gm.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<GameMod> searchGameModByName(@Param("name") @NotBlank(message = "Mod name is required") String name);

    @Query("SELECT gm.id FROM GameMod gm WHERE gm.id IN :modIds")
    Set<Long> findExistingModIds(@Param("modIds") List<Long> modIds);
}