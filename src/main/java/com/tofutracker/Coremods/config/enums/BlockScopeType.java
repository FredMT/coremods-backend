package com.tofutracker.Coremods.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BlockScopeType {

    MOD("Specific Mod"),
    AUTHOR_GLOBAL("All Mods from Author"),
    DIRECT_MESSAGES("Direct Messages"),
    INTERACTION("General Interaction");

    @Getter
    private final String displayName;
}