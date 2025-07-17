package com.tofutracker.Coremods.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BugReportPriority {

    NOT_SET("Not set"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    @Getter
    private final String displayName;
}