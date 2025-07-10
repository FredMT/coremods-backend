package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.config.enums.Role;
import com.tofutracker.Coremods.dto.ApiResponse;
import com.tofutracker.Coremods.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasAuthority('user:update_role')")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam Role role) {
        
        log.info("Attempting to update role for user ID: {} to {}", userId, role);
        
            userService.updateUserRole(userId, role);
            return ResponseEntity.ok(
                ApiResponse.success("User role updated successfully. User's sessions have been terminated.")
            );
        
    }
} 