package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.mods.permissions.CreateOrUpdateModPermissionsRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModPermissions;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModPermissionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @PathVariable("modId") GameMod gameMod,
            @Valid @RequestBody CreateOrUpdateModPermissionsRequest request,
            @AuthenticationPrincipal User currentUser) {

        return modPermissionsService.createModPermissions(gameMod, request, currentUser);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateModPermissions(
            @PathVariable("modId") GameMod gameMod,
            @Valid @RequestBody CreateOrUpdateModPermissionsRequest request,
            @AuthenticationPrincipal User currentUser) {
        return modPermissionsService.updateModPermissions(gameMod, request, currentUser);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ModPermissions>> getLatestModPermissions(
            @PathVariable("modId") GameMod gameMod) {
        return modPermissionsService.getLatestModPermissions(gameMod);
    }
}
