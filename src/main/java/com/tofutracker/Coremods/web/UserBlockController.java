package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/{userId}/blocks")
@RequiredArgsConstructor
public class UserBlockController {

    private final UserBlockService userBlockService;

    @PostMapping("/mod/{modId}")
    public ResponseEntity<ApiResponse<Void>> blockUserFromMod(@PathVariable("userId") User targetUser,
            @PathVariable("modId") GameMod gameMod, @AuthenticationPrincipal User currentUser) {
        return userBlockService.blockUserFromMod(targetUser, gameMod, currentUser);
    }
}
