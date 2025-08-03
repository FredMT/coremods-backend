package com.tofutracker.Coremods.web;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tofutracker.Coremods.dto.requests.UserImageUploadRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.UserService;
import com.tofutracker.Coremods.services.auth.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        return authService.getCurrentUser();
    }

    @PostMapping(value = "/avatar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<Void>> uploadAvatar(@Valid UserImageUploadRequest request, @AuthenticationPrincipal User currentUser) {
        userService.uploadUserImage(request.getFile(), currentUser);
        return ResponseEntity.ok(ApiResponse.success("Avatar uploaded successfully", null));
    }

    @DeleteMapping(value = "/avatar", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<Void>> deleteAvatar(@AuthenticationPrincipal User currentUser) {
        userService.deleteUserAvatar(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Avatar deleted successfully", null));
    }
}
