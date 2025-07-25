package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModFileRepository extends JpaRepository<ModFile, Long> {
    
    List<ModFile> findByMod(GameMod mod);
    
    List<ModFile> findByModAndCategory(GameMod mod, FileCategory category);
    
    Optional<ModFile> findByStorageKey(String storageKey);
    
    void deleteByMod(GameMod mod);
} 