package com.tofutracker.Coremods.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tofutracker.Coremods.entity.ModTagVote;

@Repository
public interface ModTagVoteRepository extends JpaRepository<ModTagVote, Long> {

    Optional<ModTagVote> findByModTagIdAndUserId(Long modTagId, Long userId);

}