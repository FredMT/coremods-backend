package com.tofutracker.Coremods.dto.responses.mods.bug_reports;

import com.tofutracker.Coremods.config.enums.BugReportPriority;
import com.tofutracker.Coremods.config.enums.BugReportStatus;
import com.tofutracker.Coremods.entity.BugReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugReportResponse {

    private Long id;

    private Long userId;
    private String username;

    private Long modId;
    private String modName;

    private String title;
    private String description;
    private BugReportStatus status;
    private BugReportPriority priority;
    private Boolean bugStatusOpen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BugReportResponse fromEntity(BugReport bugReport) {
        if (bugReport == null) {
            return null;
        }

        return BugReportResponse.builder()
                .id(bugReport.getId())
                .userId(bugReport.getUser().getId())
                .username(bugReport.getUser().getUsername())
                .modId(bugReport.getMod().getId())
                .modName(bugReport.getMod().getName())
                .title(bugReport.getTitle())
                .description(bugReport.getDescription())
                .status(bugReport.getStatus())
                .priority(bugReport.getPriority())
                .bugStatusOpen(bugReport.getBugStatusOpen())
                .createdAt(bugReport.getCreatedAt())
                .updatedAt(bugReport.getUpdatedAt())
                .build();
    }
}