package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.dto.*;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            RegisterRequest request, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed", errors));
        }

        try {
            userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "User registered successfully. Please check your email to verify your account."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error during user registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed. Please try again."));
        }
    }

    public ResponseEntity<ApiResponse<Void>> verifyEmail(String token) {
        try {
            boolean verified = emailVerificationService.verifyEmailToken(token);

            if (verified) {
                return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
            } else {
                throw new BadRequestException("Invalid or expired verification token");
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during email verification", e);
            throw new BadRequestException("Email verification failed. Please try again.");
        }
    }

    public ResponseEntity<ApiResponse<Void>> resendVerification(String email) {
        try {
            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isEmpty()) {
                throw new ResourceNotFoundException("User", "email", email);
            }

            User user = userOpt.get();

            if (user.getEmailVerified()) {
                throw new BadRequestException("Email is already verified");
            }

            emailVerificationService.resendVerificationToken(user);

            return ResponseEntity.ok(ApiResponse.success("Verification email sent successfully"));

        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error resending verification email", e);
            throw new BadRequestException("Failed to send verification email. Please try again.");
        }
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        
        try {
            User authenticatedUser = (User) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findById(authenticatedUser.getId());
            
            if (currentUser.isEmpty()) {
                SecurityContextHolder.clearContext();
                
                try {
                    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                    HttpServletResponse response = requestAttributes.getResponse();
                    
                    if (response != null) {
                        Cookie cookie = new Cookie("JSESSIONID", null);
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                } catch (Exception e) {
                    log.warn("Could not clear session cookie", e);
                }
                
                throw new UnauthorizedException("User no longer exists");
            }
            
            Map<String, Object> userData = getUserObjectMap(authentication);
            return ResponseEntity.ok(ApiResponse.success("User data retrieved", userData));
        } catch (ClassCastException e) {
            log.error("Error casting authentication principal to User", e);
            throw new UnauthorizedException("Authentication error");
        }
    }
    
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
    
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> resetPassword(ResetPasswordRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed", errors));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }

        try {
            User user = (User) authentication.getPrincipal();
            
            // Validate current password
            if (!userService.validateCurrentPassword(user, request.getCurrentPassword())) {
                throw new BadRequestException("Current password is incorrect");
            }

            // Update password
            userService.updatePassword(user, request.getNewPassword());

            return ResponseEntity.ok(ApiResponse.success("Password updated successfully"));

        } catch (ClassCastException e) {
            log.error("Error casting authentication principal to User", e);
            throw new UnauthorizedException("Authentication error");
        } catch (BadRequestException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during password reset", e);
            throw new BadRequestException("Password reset failed. Please try again.");
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> forgotPassword(ForgotPasswordRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed", errors));
        }

        try {
            Optional<User> userOpt = userService.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                // Don't reveal if email exists or not for security
                return ResponseEntity.ok(ApiResponse.success("If an account with that email exists, a password reset link has been sent"));
            }

            User user = userOpt.get();
            passwordResetService.generatePasswordResetToken(user);

            return ResponseEntity.ok(ApiResponse.success("If an account with that email exists, a password reset link has been sent"));

        } catch (Exception e) {
            log.error("Error during forgot password request", e);
            throw new BadRequestException("Failed to process password reset request. Please try again.");
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> resetPasswordWithToken(ForgotPasswordResetRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed", errors));
        }

        try {
            // Validate and get the user from the reset token
            Optional<User> userOpt = passwordResetService.getUserByResetToken(request.getToken());
            
            if (userOpt.isEmpty()) {
                throw new BadRequestException("Invalid or expired reset token");
            }

            User user = userOpt.get();
            
            // Consume the token (delete it)
            boolean tokenValid = passwordResetService.validateAndConsumeResetToken(request.getToken());
            
            if (!tokenValid) {
                throw new BadRequestException("Invalid or expired reset token");
            }

            // Update password - using userId to ensure we have a fresh entity
            userService.updatePassword(user.getId(), request.getNewPassword());

            return ResponseEntity.ok(ApiResponse.success("Password reset successfully. You can now log in with your new password."));

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during password reset with token", e);
            throw new BadRequestException("Password reset failed. Please try again.");
        }
    }

    private static Map<String, Object> getUserObjectMap(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getId());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("emailVerified", user.getEmailVerified());
        return userData;
    }
} 