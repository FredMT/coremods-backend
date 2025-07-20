package com.tofutracker.Coremods.entity;

import com.tofutracker.Coremods.config.enums.mod_distribution.ModDistributionPermissionOption;
import com.tofutracker.Coremods.config.enums.mod_distribution.YesOrNoCreditOption;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mod_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModPermissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_id", nullable = false)
    private GameMod mod;

    @Column(name = "version_number", nullable = false)
    @Builder.Default
    private Integer versionNumber = 1;

    @Column(name = "is_latest", nullable = false)
    @Builder.Default
    private Boolean isLatest = true;

    @Column(name = "use_custom_permissions", nullable = false)
    private Boolean useCustomPermissions;

    @Column(name = "custom_permission_instructions", columnDefinition = "TEXT")
    private String customPermissionInstructions;

    @Column(name = "has_restricted_assets_from_others")
    private Boolean hasRestrictedAssetsFromOthers;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_to_other_sites")
    private YesOrNoCreditOption uploadToOtherSites;

    @Enumerated(EnumType.STRING)
    @Column(name = "convert_to_other_games")
    private YesOrNoCreditOption convertToOtherGames;

    @Enumerated(EnumType.STRING)
    @Column(name = "modify_and_reupload")
    private ModDistributionPermissionOption modifyAndReupload;

    @Enumerated(EnumType.STRING)
    @Column(name = "use_assets_in_own_files")
    private ModDistributionPermissionOption useAssetsInOwnFiles;

    @Column(name = "restrict_commercial_use")
    private Boolean restrictCommercialUse;

    @Column(name = "credits", columnDefinition = "TEXT")
    private String credits;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}