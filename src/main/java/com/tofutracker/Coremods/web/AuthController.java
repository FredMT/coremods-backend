package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.ForgotPasswordRequest;
import com.tofutracker.Coremods.dto.requests.ForgotPasswordResetRequest;
import com.tofutracker.Coremods.dto.requests.RegisterRequest;
import com.tofutracker.Coremods.dto.requests.ResetPasswordRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.services.auth.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        return authService.register(request, bindingResult);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
        return authService.resendVerification(email);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        return authService.getCurrentUser();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resetPassword(@Valid @RequestBody ResetPasswordRequest request, BindingResult bindingResult) {
        return authService.resetPassword(request, bindingResult);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, Object>>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, BindingResult bindingResult) {
        return authService.forgotPassword(request, bindingResult);
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resetPasswordWithToken(@Valid @RequestBody ForgotPasswordResetRequest request, BindingResult bindingResult) {
        return authService.resetPasswordWithToken(request, bindingResult);
    }
} 