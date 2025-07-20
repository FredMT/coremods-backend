package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.config.enums.mod_distribution.ModDistributionPermissionOption;
import com.tofutracker.Coremods.config.enums.mod_distribution.YesOrNoCreditOption;
import com.tofutracker.Coremods.dto.requests.mods.permissions.CreateOrUpdateModPermissionsRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModPermissions;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.ModPermissionsRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ModPermissionsService {

    private final ModPermissionsRepository modPermissionsRepository;

    public ModPermissionsService(ModPermissionsRepository modPermissionsRepository) {
        this.modPermissionsRepository = modPermissionsRepository;
    }

    @Transactional
    public ResponseEntity<ApiResponse<Void>> createModPermissions(GameMod gameMod, CreateOrUpdateModPermissionsRequest request, User currentUser) {

        if (!currentUser.getId().equals(gameMod.getAuthor().getId())) {
            throw new ForbiddenException("You are not authorized to create permissions for this mod.");
        }

        validateModPermissionsRequest(request);

        Optional<ModPermissions> existingPermissions = modPermissionsRepository.findByModIdAndIsLatestTrue(gameMod.getId());

        if (existingPermissions.isPresent()) {
            throw new BadRequestException("Mod permissions already exists.");
        }

        ModPermissions modPermissionsToSave;

        if (request.getUseCustomPermissions()) {
            modPermissionsToSave = ModPermissions.builder().mod(gameMod).versionNumber(1).isLatest(true).useCustomPermissions(request.getUseCustomPermissions()).customPermissionInstructions(request.getCustomPermissionInstructions()).credits(request.getCredits()).build();
        } else {
            modPermissionsToSave = ModPermissions.builder().mod(gameMod).versionNumber(1).isLatest(true).useCustomPermissions(request.getUseCustomPermissions()).hasRestrictedAssetsFromOthers(request.getHasRestrictedAssetsFromOthers()).uploadToOtherSites(YesOrNoCreditOption.valueOf(request.getUploadToOtherSites())).convertToOtherGames(YesOrNoCreditOption.valueOf(request.getConvertToOtherGames())).modifyAndReupload(ModDistributionPermissionOption.valueOf(request.getModifyAndReupload())).useAssetsInOwnFiles(ModDistributionPermissionOption.valueOf(request.getUseAssetsInOwnFiles())).restrictCommercialUse(request.getRestrictCommercialUse()).credits(request.getCredits()).build();
        }

        modPermissionsRepository.save(modPermissionsToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Mod permissions saved successfully"));
    }

    @Transactional
    public ResponseEntity<ApiResponse<Void>> updateModPermissions(GameMod gameMod, @Valid CreateOrUpdateModPermissionsRequest request, User currentUser) {

        if (!currentUser.getId().equals(gameMod.getAuthor().getId())) {
            throw new ForbiddenException("You are not authorized to update permissions for this mod.");
        }

        validateModPermissionsRequest(request);

        Optional<ModPermissions> latestPermissionsOpt = modPermissionsRepository.findByModIdAndIsLatestTrue(gameMod.getId());

        if (latestPermissionsOpt.isEmpty()) {
            throw new BadRequestException("Mod permissions not found.");
        }

        ModPermissions latestPermissions = latestPermissionsOpt.get();

        latestPermissions.setIsLatest(false);
        modPermissionsRepository.saveAndFlush(latestPermissions);

        Integer nextVersionNumber = latestPermissions.getVersionNumber() + 1;

        ModPermissions newPermissions;

        if (request.getUseCustomPermissions()) {
            newPermissions = ModPermissions.builder().mod(gameMod).versionNumber(nextVersionNumber).isLatest(true).useCustomPermissions(request.getUseCustomPermissions()).customPermissionInstructions(request.getCustomPermissionInstructions()).credits(request.getCredits()).build();
        } else {
            newPermissions = ModPermissions.builder().mod(gameMod).versionNumber(nextVersionNumber).isLatest(true).useCustomPermissions(request.getUseCustomPermissions()).hasRestrictedAssetsFromOthers(request.getHasRestrictedAssetsFromOthers()).uploadToOtherSites(YesOrNoCreditOption.valueOf(request.getUploadToOtherSites())).convertToOtherGames(YesOrNoCreditOption.valueOf(request.getConvertToOtherGames())).modifyAndReupload(ModDistributionPermissionOption.valueOf(request.getModifyAndReupload())).useAssetsInOwnFiles(ModDistributionPermissionOption.valueOf(request.getUseAssetsInOwnFiles())).restrictCommercialUse(request.getRestrictCommercialUse()).credits(request.getCredits()).build();
        }

        modPermissionsRepository.save(newPermissions);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Mod permissions updated successfully"));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<ModPermissions>> getLatestModPermissions(GameMod gameMod) {
        ModPermissions modPermissions = modPermissionsRepository.
                findByModIdAndIsLatestTrue(gameMod.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("No permissions found for mod with ID: " + gameMod.getId()));

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Latest mod permissions retrieved successfully", modPermissions));
    }

    private void validateModPermissionsRequest(CreateOrUpdateModPermissionsRequest request) {
        if (request == null) {
            throw new BadRequestException("Request cannot be null");
        }

        List<String> errors = new ArrayList<>();

        Boolean useCustom = request.getUseCustomPermissions();
        if (useCustom == null) {
            errors.add("Use custom permissions flag is required");
        } else if (useCustom) {
            validateCustomPermissions(request, errors);
        } else {
            validateStandardPermissions(request, errors);
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Validation failed", errors);
        }
    }

    private void validateCustomPermissions(CreateOrUpdateModPermissionsRequest request, List<String> errors) {
        if (!StringUtils.hasText(request.getCustomPermissionInstructions())) {
            errors.add("Custom permission instructions are required when using custom permissions");
        }
    }

    private void validateStandardPermissions(CreateOrUpdateModPermissionsRequest request, List<String> errors) {
        if (request.getHasRestrictedAssetsFromOthers() == null) {
            errors.add("Restricted assets from the other sites flag is required");
        }
        if (request.getUploadToOtherSites() == null) {
            errors.add("Upload to other sites permission field is required when not using custom permissions");
        }
        if (request.getConvertToOtherGames() == null) {
            errors.add("Convert to other games permission field is required when not using custom permissions");
        }
        if (request.getModifyAndReupload() == null) {
            errors.add("Modify and reupload permission field is required when not using custom permissions");
        }
        if (request.getUseAssetsInOwnFiles() == null) {
            errors.add("Use assets in own files permission field is required when not using custom permissions");
        }
        if (request.getRestrictCommercialUse() == null) {
            errors.add("Restrict commercial use flag is required when not using custom permissions");
        }
    }
}
