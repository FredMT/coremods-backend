package com.tofutracker.Coremods.dto.responses.mods.bug_reports;

import com.tofutracker.Coremods.config.enums.BugReportPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugReportPriorityUpdateResponse {

    private Long bugReportId;
    private BugReportPriority priority;
}