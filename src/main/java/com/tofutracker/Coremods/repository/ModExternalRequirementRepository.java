package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.ModExternalRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModExternalRequirementRepository extends JpaRepository<ModExternalRequirement, Long> {

}