package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.ModDistributionSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModDistributionSettingsRepository extends JpaRepository<ModDistributionSettings, Long> {

    Optional<ModDistributionSettings> findByModId(Long modId);

}