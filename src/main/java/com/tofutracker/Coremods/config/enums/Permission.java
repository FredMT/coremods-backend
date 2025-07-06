package com.tofutracker.Coremods.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    // Mod permissions
    MOD_VIEW("mod:view"),         // View mods
    MOD_UPLOAD("mod:upload"),     // Upload mods (pending approval)
    MOD_DOWNLOAD("mod:download"), // Download mods
    MOD_APPROVE("mod:approve"),   // Approve or decline mods
    MOD_DELETE("mod:delete"),     // Delete mods
    
    // Comment permissions
    COMMENT_CREATE("comment:create"),   // Create comments
    COMMENT_VIEW("comment:view"),       // View comments
    COMMENT_REPLY("comment:reply"),     // Reply to comments
    COMMENT_DELETE("comment:delete"),   // Delete comments
    
    // User permissions
    USER_BAN("user:ban"),              // Ban users
    USER_UPDATE_ROLE("user:update_role") // Update user roles
    
    ;

    @Getter
    private final String permission;
} 