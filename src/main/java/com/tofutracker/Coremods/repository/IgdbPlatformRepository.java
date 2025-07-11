package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.IgdbPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IgdbPlatformRepository extends JpaRepository<IgdbPlatform, Long> {
} 