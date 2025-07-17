package com.tofutracker.Coremods.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tofutracker.Coremods.entity.ModTag;

@Repository
public interface ModTagRepository extends JpaRepository<ModTag, Long> {

}