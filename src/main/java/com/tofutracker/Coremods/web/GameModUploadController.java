package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModDetailsRequest;
import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModRequirementsMirrorsRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModRequirementsMirrorsService;
import com.tofutracker.Coremods.services.mods.ModUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mods")
@RequiredArgsConstructor
@Slf4j
public class GameModUploadController {

    private final ModUploadService modUploadService;
    private final ModRequirementsMirrorsService modRequirementsMirrorsService;

    @PostMapping("/upload/details")
    public ResponseEntity<ApiResponse<Void>> saveModDetails(
            @Valid @RequestBody ModDetailsRequest modDetailsRequest,
            @AuthenticationPrincipal User currentUser) {

        modUploadService.saveModDetails(modDetailsRequest, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Mod details saved successfully"));
    }

    @PostMapping("/{modId}/upload/requirements-mirrors")
    public ResponseEntity<ApiResponse<Void>> saveModRequirementsMirrors(
            @Valid @RequestBody ModRequirementsMirrorsRequest request,
            @PathVariable("modId") Long modId,
            @AuthenticationPrincipal User currentUser) {

        modRequirementsMirrorsService.saveModRequirementsMirrors(request, modId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Mod requirements and mirrors saved successfully"));
    }

    @PostMapping("/{modId}/publish")
    public  ResponseEntity<ApiResponse<Void>> publishMod(
            @PathVariable("modId") Long modId,
            @AuthenticationPrincipal User currentUser
    ) {
        modUploadService.publishMod(modId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Mod published successfully"));
    }
}