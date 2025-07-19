package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.config.enums.BlockScopeType;
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
        return userBlockService.blockUser(targetUser, currentUser, BlockScopeType.MOD, gameMod);
    }

    @DeleteMapping("/mod/{modId}")
    public ResponseEntity<ApiResponse<Void>> unblockUserFromMod(@PathVariable("userId") User targetUser,
            @PathVariable("modId") GameMod gameMod, @AuthenticationPrincipal User currentUser) {
        return userBlockService.unblockUser(targetUser, currentUser, BlockScopeType.MOD, gameMod);
    }

    @PostMapping("/global")
    public ResponseEntity<ApiResponse<Void>> blockUserFromAuthorGlobally(@PathVariable("userId") User targetUser,
            @AuthenticationPrincipal User currentUser) {
        return userBlockService.blockUser(targetUser, currentUser, BlockScopeType.AUTHOR_GLOBAL, null);
    }

    @DeleteMapping("/global")
    public ResponseEntity<ApiResponse<Void>> unblockUserFromAuthorGlobally(@PathVariable("userId") User targetUser,
            @AuthenticationPrincipal User currentUser) {
        return userBlockService.unblockUser(targetUser, currentUser, BlockScopeType.AUTHOR_GLOBAL, null);
    }

    @PostMapping("/dm")
    public ResponseEntity<ApiResponse<Void>> blockUserFromDM(@PathVariable("userId") User targetUser,
            @AuthenticationPrincipal User currentUser) {
        return userBlockService.blockUser(targetUser, currentUser, BlockScopeType.DIRECT_MESSAGES, null);
    }

    @DeleteMapping("/dm")
    public ResponseEntity<ApiResponse<Void>> unblockUserFromDirectMessages(@PathVariable("userId") User targetUser,
            @AuthenticationPrincipal User currentUser) {
        return userBlockService.unblockUser(targetUser, currentUser, BlockScopeType.DIRECT_MESSAGES, null);
    }

    @PostMapping("/interaction")
    public ResponseEntity<ApiResponse<Void>> blockUserFromInteraction(@PathVariable("userId") User targetUser,
            @AuthenticationPrincipal User currentUser) {
        return userBlockService.blockUser(targetUser, currentUser, BlockScopeType.INTERACTION, null);
    }

    @DeleteMapping("/interaction")
    public ResponseEntity<ApiResponse<Void>> unblockUserFromInteraction(@PathVariable("userId") User targetUser,
            @AuthenticationPrincipal User currentUser) {
        return userBlockService.unblockUser(targetUser, currentUser, BlockScopeType.INTERACTION, null);
    }
}
