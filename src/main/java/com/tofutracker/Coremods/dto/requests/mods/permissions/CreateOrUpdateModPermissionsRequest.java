package com.tofutracker.Coremods.dto.requests.mods.permissions;

import com.tofutracker.Coremods.config.enums.mod_distribution.ModDistributionPermissionOption;
import com.tofutracker.Coremods.config.enums.mod_distribution.YesOrNoCreditOption;
import com.tofutracker.Coremods.dto.annotation.ValueOfEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateModPermissionsRequest {

    @NotNull
    private Boolean useCustomPermissions;

    // Required if useCustomPermissions = true
    private String customPermissionInstructions;

    // Fields below are required if useCustomPermissions = false
    private Boolean hasRestrictedAssetsFromOthers;

    @ValueOfEnum(enumClass = YesOrNoCreditOption.class, message = "Upload to other sites must be YES_CREDIT or NO")
    private String uploadToOtherSites;

    @ValueOfEnum(enumClass = YesOrNoCreditOption.class, message = "Convert to other games must be YES_CREDIT or NO")
    private String convertToOtherGames;

    @ValueOfEnum(enumClass = ModDistributionPermissionOption.class, message = "Modify and reupload must be YES_NO_CREDIT, YES_CREDIT, NOT_WITHOUT_PERMISSION, or ABSOLUTELY_NOT")
    private String modifyAndReupload;

    @ValueOfEnum(enumClass = ModDistributionPermissionOption.class, message = "Use assets in own files must be YES_NO_CREDIT, YES_CREDIT, NOT_WITHOUT_PERMISSION, or ABSOLUTELY_NOT")
    private String useAssetsInOwnFiles;

    private Boolean restrictCommercialUse;

    private String credits;
}
