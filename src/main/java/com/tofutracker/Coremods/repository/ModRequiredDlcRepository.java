package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.ModRequiredDlc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModRequiredDlcRepository extends JpaRepository<ModRequiredDlc, Long> {

}