package com.tofutracker.Coremods.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tofutracker.Coremods.config.enums.Role;
import com.tofutracker.Coremods.dto.ApiResponse;
import com.tofutracker.Coremods.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/role")
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