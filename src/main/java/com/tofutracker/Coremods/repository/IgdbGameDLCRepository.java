package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.IgdbGame;
import com.tofutracker.Coremods.entity.IgdbGameDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IgdbGameDLCRepository extends JpaRepository<IgdbGameDLC, Long> {
    boolean existsByParentGameIdAndDlcId(Long parentGameId, Long dlcId);

    @Query("SELECT g FROM IgdbGame g WHERE g.id IN (SELECT d.dlcId FROM IgdbGameDLC d WHERE d.parentGame.id = :gameId)")
    List<IgdbGame> findAllDLCGamesByParentGameId(@Param("gameId") Long gameId);

    @Query("SELECT d.dlcId FROM IgdbGameDLC d WHERE d.parentGame.id = :gameId")
    Set<Long> findDlcIdsByParentGameId(@Param("gameId") Long gameId);

    @Query("SELECT d.dlcId FROM IgdbGameDLC d WHERE d.parentGame.id = :gameId AND d.dlcId IN :dlcIds")
    Set<Long> findValidDlcIdsByParentGameIdAndDlcIds(@Param("gameId") Long gameId, @Param("dlcIds") List<Long> dlcIds);
}