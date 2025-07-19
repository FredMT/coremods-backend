package com.tofutracker.Coremods.dto.requests.mods.bug_reports;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentBugReportRequest {

    @NotBlank(message = "Comment content is required")
    private String content;
}
