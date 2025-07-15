package com.tofutracker.Coremods.config;

import com.tofutracker.Coremods.services.email.EmailVerificationService;
import com.tofutracker.Coremods.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredTokens() {
        try {
            emailVerificationService.cleanupExpiredTokens();
            passwordResetService.cleanupExpiredTokens();
            log.info("Expired token cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during expired token cleanup", e);
        }
    }
} 