package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.ModRequiredMod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModRequiredModRepository extends JpaRepository<ModRequiredMod, Long> {

}