package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.GameModCategory;
import com.tofutracker.Coremods.entity.IgdbGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameModCategoryRepository extends JpaRepository<GameModCategory, Long> {

    List<GameModCategory> findByGameId(Long gameId);

    Optional<GameModCategory> findByGameAndCategoryNameAndApproved(IgdbGame game, String categoryName, boolean approved);
    
    List<GameModCategory> findByGameAndParentIsNull(IgdbGame game);
    
    List<GameModCategory> findByParent(GameModCategory parent);
    
    @Query("SELECT c FROM GameModCategory c WHERE c.game = :game AND c.parent IS NULL")
    List<GameModCategory> findRootCategoriesByGame(@Param("game") IgdbGame game);
    
    @Query("SELECT c FROM GameModCategory c LEFT JOIN FETCH c.children WHERE c.game = :game AND c.parent IS NULL")
    List<GameModCategory> findRootCategoriesWithChildrenByGame(@Param("game") IgdbGame game);
    
    Optional<GameModCategory> findByGameAndCategoryNameAndParent(IgdbGame game, String categoryName, GameModCategory parent);
    
    @Query("SELECT c FROM GameModCategory c WHERE c.game = :game AND LOWER(c.categoryName) = LOWER(:categoryName) AND c.parent = :parent")
    Optional<GameModCategory> findByGameAndCategoryNameIgnoreCaseAndParent(@Param("game") IgdbGame game, @Param("categoryName") String categoryName, @Param("parent") GameModCategory parent);
    
    long countByGameAndParentIsNull(IgdbGame game);
} 