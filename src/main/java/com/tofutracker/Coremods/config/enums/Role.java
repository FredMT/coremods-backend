package com.tofutracker.Coremods.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tofutracker.Coremods.config.enums.Permission.*;

@RequiredArgsConstructor
public enum Role {

    USER(
        Set.of(
            // Mod permissions
            MOD_VIEW,         // View mods
            MOD_UPLOAD,       // Upload mods (pending approval)
            MOD_DOWNLOAD,     // Download mods
            
            // Comment permissions
            COMMENT_CREATE,   // Create comments
            COMMENT_VIEW,     // View comments
            COMMENT_REPLY     // Reply to comments
        )
    ),
    
    MODERATOR(
        Set.of(
            // User permissions - all user permissions plus:
            MOD_VIEW,         // View mods
            MOD_UPLOAD,       // Upload mods (pending approval)
            MOD_DOWNLOAD,     // Download mods
            COMMENT_CREATE,   // Create comments
            COMMENT_VIEW,     // View comments
            COMMENT_REPLY,    // Reply to comments
            
            // Moderator permissions
            MOD_APPROVE,      // Approve or decline mods
            MOD_DELETE,       // Delete mods
            MOD_CATEGORY_APPROVE, // Approve or decline mod categories
            COMMENT_DELETE,   // Delete comments
            USER_BAN          // Ban users
        )
    ),
    
    ADMIN(
        Set.of(
            // All user permissions
            MOD_VIEW,         // View mods
            MOD_UPLOAD,       // Upload mods (pending approval)
            MOD_DOWNLOAD,     // Download mods
            COMMENT_CREATE,   // Create comments
            COMMENT_VIEW,     // View comments
            COMMENT_REPLY,    // Reply to comments
            
            // All moderator permissions
            MOD_APPROVE,      // Approve or decline mods
            MOD_DELETE,       // Delete mods
            MOD_CATEGORY_APPROVE, // Approve or decline mod categories
            COMMENT_DELETE,   // Delete comments
            USER_BAN,         // Ban users
            
            // Admin-only permissions
            USER_UPDATE_ROLE  // Update user roles
        )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}