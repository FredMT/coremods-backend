package com.tofutracker.Coremods.dto.requests.bug_report;

import com.tofutracker.Coremods.config.enums.BugReportStatus;
import com.tofutracker.Coremods.dto.annotation.ValueOfEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBugReportStatusRequest {

    @NotNull(message = "Status is required")
    @ValueOfEnum(enumClass = BugReportStatus.class, message = "Invalid bug report status")
    private String status;
}