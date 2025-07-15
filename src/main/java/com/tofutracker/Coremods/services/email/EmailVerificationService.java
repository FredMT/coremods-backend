package com.tofutracker.Coremods.services.email;

import com.tofutracker.Coremods.entity.EmailVerificationToken;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.repository.EmailVerificationTokenRepository;
import com.tofutracker.Coremods.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserService userService;

    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository,
                                    EmailService emailService,
                                    @Lazy UserService userService) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Value("${app.security.email-verification.expiry:24}")
    private int tokenExpiryHours;

    private final SecureRandom secureRandom = new SecureRandom();

    public void generateVerificationToken(User user) {

        tokenRepository.deleteByUser(user);

        String token = generateSecureToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(tokenExpiryHours);

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();

        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user, token);
    }

    @Transactional
    public boolean verifyEmailToken(String token) {

        Optional<EmailVerificationToken> verificationTokenOpt = tokenRepository.findByToken(token);
        
        if (verificationTokenOpt.isEmpty()) {
            log.warn("Invalid verification token: {}", token);
            return false;
        }

        EmailVerificationToken verificationToken = verificationTokenOpt.get();
        
        if (verificationToken.isExpired()) {
            log.warn("Expired verification token for user: {}", verificationToken.getUser().getUsername());
            return false;
        }

        User user = verificationToken.getUser();
        Long userId = user.getId();
        String username = user.getUsername();
        

        tokenRepository.delete(verificationToken);

        userService.markEmailAsVerified(userId);

        try {
            emailService.sendWelcomeEmail(user);
        } catch (Exception e) {
            log.warn("Failed to send welcome email to user: {} (non-critical)", username, e);
        }

        return true;
    }

    public void resendVerificationToken(User user) {
        if (user.getEmailVerified()) {
            throw new IllegalStateException("Email is already verified for user: " + user.getUsername());
        }

        generateVerificationToken(user);
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