package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.ModDistributionSettingsPermissionsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModDistributionSettingsPermissionsLogRepository
        extends JpaRepository<ModDistributionSettingsPermissionsLog, Long> {
}