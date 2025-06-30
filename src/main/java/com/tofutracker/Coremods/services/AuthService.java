package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.dto.ApiResponse;
import com.tofutracker.Coremods.dto.RegisterRequest;
import com.tofutracker.Coremods.entity.User;
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
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid or expired verification token"));
            }
        } catch (Exception e) {
            log.error("Error during email verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Email verification failed. Please try again."));
        }
    }

    public ResponseEntity<ApiResponse<Void>> resendVerification(String email) {
        try {
            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User with this email does not exist"));
            }

            User user = userOpt.get();

            if (user.getEmailVerified()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email is already verified"));
            }

            emailVerificationService.resendVerificationToken(user);

            return ResponseEntity.ok(ApiResponse.success("Verification email sent successfully"));

        } catch (Exception e) {
            log.error("Error resending verification email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send verification email. Please try again."));
        }
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
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

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User no longer exists"));
            }

            Map<String, Object> userData = getUserObjectMap(authentication);
            return ResponseEntity.ok(ApiResponse.success("User data retrieved", userData));
        } catch (ClassCastException e) {
            log.error("Error casting authentication principal to User", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Authentication error"));
        }
    }

    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
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