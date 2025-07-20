package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.ModPermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModPermissionsRepository extends JpaRepository<ModPermissions, Long> {

    Optional<ModPermissions> findByModIdAndIsLatestTrue(Long modId);

    List<ModPermissions> findByModIdOrderByVersionNumberDesc(Long modId);
}