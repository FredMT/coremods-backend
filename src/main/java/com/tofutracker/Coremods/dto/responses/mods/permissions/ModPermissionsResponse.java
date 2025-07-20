package com.tofutracker.Coremods.dto.responses.mods.permissions;

import com.tofutracker.Coremods.config.enums.mod_distribution.ModDistributionPermissionOption;
import com.tofutracker.Coremods.config.enums.mod_distribution.YesOrNoCreditOption;
import com.tofutracker.Coremods.entity.ModPermissions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModPermissionsResponse {
    private Long id;
    private Long modId;
    private Integer versionNumber;
    private Boolean isLatest;
    private Boolean useCustomPermissions;
    private String customPermissionInstructions;
    private Boolean hasRestrictedAssetsFromOthers;
    private YesOrNoCreditOption uploadToOtherSites;
    private YesOrNoCreditOption convertToOtherGames;
    private ModDistributionPermissionOption modifyAndReupload;
    private ModDistributionPermissionOption useAssetsInOwnFiles;
    private Boolean restrictCommercialUse;
    private String credits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ModPermissionsResponse fromEntity(ModPermissions entity) {
        if (entity == null) {
            return null;
        }

        return ModPermissionsResponse.builder()
                .id(entity.getId())
                .modId(entity.getMod().getId())
                .versionNumber(entity.getVersionNumber())
                .isLatest(entity.getIsLatest())
                .useCustomPermissions(entity.getUseCustomPermissions())
                .customPermissionInstructions(entity.getCustomPermissionInstructions())
                .hasRestrictedAssetsFromOthers(entity.getHasRestrictedAssetsFromOthers())
                .uploadToOtherSites(entity.getUploadToOtherSites())
                .convertToOtherGames(entity.getConvertToOtherGames())
                .modifyAndReupload(entity.getModifyAndReupload())
                .useAssetsInOwnFiles(entity.getUseAssetsInOwnFiles())
                .restrictCommercialUse(entity.getRestrictCommercialUse())
                .credits(entity.getCredits())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}