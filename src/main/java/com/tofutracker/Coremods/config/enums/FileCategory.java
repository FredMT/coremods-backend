package com.tofutracker.Coremods.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FileCategory {
    MAIN_FILES("Main Files"),
    UPDATES("Updates"),
    OPTIONAL_FILES("Optional Files"),
    OLD_VERSIONS("Old Versions"),
    MISCELLANEOUS("Miscellaneous"),
    ARCHIVES("Archived");


    @Getter
    private final String displayName;
}
