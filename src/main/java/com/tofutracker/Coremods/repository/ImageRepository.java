package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.Image;
import com.tofutracker.Coremods.entity.GameMod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    
    List<Image> findByGameModIdOrderByDisplayOrderAsc(Long gameModId);
    @Query("SELECT i FROM Image i WHERE i.gameMod.id = :gameModId AND i.imageType = 'HEADER'")
    Optional<Image> findHeaderImageByGameModId(@Param("gameModId") Long gameModId);
    
    @Query("SELECT i FROM Image i WHERE i.gameMod.id = :gameModId AND i.imageType = 'MOD_IMAGE' ORDER BY i.displayOrder ASC")
    List<Image> findModImagesByGameModId(@Param("gameModId") Long gameModId);
    
    @Query("SELECT COUNT(i) FROM Image i WHERE i.gameMod.id = :gameModId AND i.imageType = 'MOD_IMAGE'")
    long countModImagesByGameModId(@Param("gameModId") Long gameModId);
    
    void deleteByGameModId(Long gameModId);
} 