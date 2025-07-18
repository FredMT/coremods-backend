package com.tofutracker.Coremods.dto.responses.mods.bug_reports;

import com.tofutracker.Coremods.config.enums.BugReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugReportStatusUpdateResponse {

    private Long bugReportId;
    private BugReportStatus status;
}