package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.GameModCategory;
import com.tofutracker.Coremods.entity.IgdbGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameModCategoryRepository extends JpaRepository<GameModCategory, Long> {
    
    List<GameModCategory> findByGame(IgdbGame game);
    
    List<GameModCategory> findByGameId(Long gameId);
    
    List<GameModCategory> findByGameIdAndApproved(Long gameId, boolean approved);
    
    List<GameModCategory> findByApproved(boolean approved);
    
    Optional<GameModCategory> findByGameIdAndCategoryName(Long gameId, String categoryName);
    
    boolean existsByGameIdAndCategoryName(Long gameId, String categoryName);
} 