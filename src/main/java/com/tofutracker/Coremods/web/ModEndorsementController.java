package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModEndorsementService;
import lombok.RequiredArgsConstructor;
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
        return modEndorsementService.endorseMod(modId, currentUser);
    }

    @DeleteMapping()
    public ResponseEntity<Void> removeEndorsement(@PathVariable Long modId,
            @AuthenticationPrincipal User currentUser) {
        return modEndorsementService.removeEndorsement(modId, currentUser);
    }

}
