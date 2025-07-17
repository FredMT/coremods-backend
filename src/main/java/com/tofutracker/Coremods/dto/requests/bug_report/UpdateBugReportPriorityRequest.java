package com.tofutracker.Coremods.dto.requests.bug_report;

import com.tofutracker.Coremods.config.enums.BugReportPriority;
import com.tofutracker.Coremods.dto.annotation.ValueOfEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBugReportPriorityRequest {

    @NotNull(message = "Priority is required")
    @ValueOfEnum(enumClass = BugReportPriority.class, message = "Invalid bug report priority")
    private String priority;
}