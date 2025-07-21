package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.IgdbGamePlatform;
import com.tofutracker.Coremods.entity.IgdbGamePlatformId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IgdbGamePlatformRepository extends JpaRepository<IgdbGamePlatform, IgdbGamePlatformId> {

    List<IgdbGamePlatform> findByGameId(Long gameId);

    List<IgdbGamePlatform> findByPlatformId(Long platformId);

    boolean existsByGameIdAndPlatformId(Long gameId, Long platformId);

    void deleteByGameId(Long gameId);

    void deleteByPlatformId(Long platformId);

    @Query("SELECT gp.platformId FROM IgdbGamePlatform gp WHERE gp.gameId = :gameId")
    List<Long> findPlatformIdsByGameId(@Param("gameId") Long gameId);

    @Query("SELECT gp.gameId FROM IgdbGamePlatform gp WHERE gp.platformId = :platformId")
    List<Long> findGameIdsByPlatformId(@Param("platformId") Long platformId);
}