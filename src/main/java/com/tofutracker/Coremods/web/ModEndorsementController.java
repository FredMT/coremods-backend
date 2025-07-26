package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModEndorsementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mods/{modId}/endorsements")
@RequiredArgsConstructor
public class ModEndorsementController {
    private final ModEndorsementService modEndorsementService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> endorseMod(@PathVariable Long modId,
            @AuthenticationPrincipal User currentUser) {
        modEndorsementService.endorseMod(modId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Mod endorsed successfully"));
    }

    @DeleteMapping()
    public ResponseEntity<Void> removeEndorsement(@PathVariable Long modId,
            @AuthenticationPrincipal User currentUser) {
        modEndorsementService.removeEndorsement(modId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
