package com.tofutracker.Coremods.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BugReportStatus {

    NEW_ISSUE("New Issue"),
    BEING_LOOKED_AT("Being looked at"),
    FIXED("Fixed"),
    KNOWN_ISSUES("Known issues"),
    DUPLICATES("Duplicates"),
    NOT_A_BUG("Not a bug"),
    WONT_FIX("Won't fix"),
    NEED_MORE_INFO("Need more info");

    @Getter
    private final String displayName;
}