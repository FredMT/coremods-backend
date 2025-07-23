package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.ModMirror;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModMirrorRepository extends JpaRepository<ModMirror, Long> {

}