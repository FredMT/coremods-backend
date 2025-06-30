package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.entity.PasswordResetToken;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.repository.PasswordResetTokenRepository;
import com.tofutracker.Coremods.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.security.password-reset.expiry-minutes:5}")
    private int tokenExpiryMinutes;

    private final SecureRandom secureRandom = new SecureRandom();

    public void generatePasswordResetToken(User user) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        String token = generateSecureToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(tokenExpiryMinutes);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();

        tokenRepository.save(resetToken);

        // Send password reset email
        emailService.sendPasswordResetEmail(user, token);
    }

    @Transactional
    public boolean validateAndConsumeResetToken(String token) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        
        if (resetTokenOpt.isEmpty()) {
            log.warn("Invalid password reset token: {}", token);
            return false;
        }

        PasswordResetToken resetToken = resetTokenOpt.get();
        
        if (resetToken.isExpired()) {
            log.warn("Expired password reset token for user: {}", resetToken.getUser().getUsername());
            tokenRepository.delete(resetToken);
            return false;
        }

        // Token is valid, delete it (one-time use)
        tokenRepository.delete(resetToken);
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByResetToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(resetToken -> !resetToken.isExpired())
                .map(resetToken -> {
                    // Get the user ID from the token and fetch a fresh instance from the repository
                    Long userId = resetToken.getUser().getId();
                    return userRepository.findById(userId).orElse(null);
                });
    }

    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
} 