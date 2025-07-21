package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.IgdbGameDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IgdbGameDLCRepository extends JpaRepository<IgdbGameDLC, Long> {
    boolean existsByParentGameIdAndDlcId(Long parentGameId, Long dlcId);
}