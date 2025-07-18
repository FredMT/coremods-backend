package com.tofutracker.Coremods.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tofutracker.Coremods.entity.ModEndorsement;

@Repository
public interface ModEndorsementRepository extends JpaRepository<ModEndorsement, Long> {

    Optional<ModEndorsement> findByModIdAndUserId(Long modId, Long userId);
}