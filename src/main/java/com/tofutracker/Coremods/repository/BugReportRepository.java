package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugReportRepository extends JpaRepository<BugReport, Long> {

    /**
     * Find all bug reports for a specific mod
     */
    List<BugReport> findByModIdOrderByCreatedAtDesc(Long modId);
}