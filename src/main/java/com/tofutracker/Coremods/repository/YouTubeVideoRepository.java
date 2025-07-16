package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.YouTubeVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeVideoRepository extends JpaRepository<YouTubeVideo, Long> {
    
    List<YouTubeVideo> findByGameModIdOrderByDisplayOrderAsc(Long gameModId);
    
    @Query("SELECT COUNT(yv) FROM YouTubeVideo yv WHERE yv.gameMod.id = :gameModId")
    long countByGameModId(@Param("gameModId") Long gameModId);
    
    void deleteByGameModId(Long gameModId);
    
    boolean existsByGameModIdAndIdentifier(Long gameModId, String identifier);
} 