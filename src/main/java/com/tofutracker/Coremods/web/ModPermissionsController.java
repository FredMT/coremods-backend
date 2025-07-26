package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.mods.permissions.CreateOrUpdateModPermissionsRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.ModPermissions;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModPermissionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mods/{modId}/permissions")
@RequiredArgsConstructor
public class ModPermissionsController {

    private final ModPermissionsService modPermissionsService;

    @PostMapping

    public ResponseEntity<ApiResponse<Void>> createModPermissions(
            @PathVariable("modId") Long gameModId,
            @Valid @RequestBody CreateOrUpdateModPermissionsRequest request,
            @AuthenticationPrincipal User currentUser) {

        modPermissionsService.createModPermissions(gameModId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Mod permissions saved successfully"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateModPermissions(
            @PathVariable("modId") Long gameModId,
            @Valid @RequestBody CreateOrUpdateModPermissionsRequest request,
            @AuthenticationPrincipal User currentUser) {
        modPermissionsService.updateModPermissions(gameModId, request, currentUser);

        return ResponseEntity.ok(ApiResponse.success("Mod permissions updated successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ModPermissions>> getLatestModPermissions(
            @PathVariable("modId") Long gameModId) {
        ModPermissions modPermissions = modPermissionsService.getLatestModPermissions(gameModId);
        return ResponseEntity.ok(ApiResponse.success("Latest mod permissions retrieved successfully", modPermissions));
    }
}
