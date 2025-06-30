package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final ResendEmailService resendEmailService;
    private final EmailTemplateService emailTemplateService;

    public void sendVerificationEmail(User user, String token) {

        try {
            EmailTemplateService.EmailContent emailContent = 
                emailTemplateService.generateVerificationEmail(user, token);

            boolean emailSent = resendEmailService.sendEmailWithPlainText(
                user.getEmail(),
                emailContent.subject(),
                emailContent.htmlContent(),
                emailContent.textContent()
            );
            
            if (!emailSent) {
                log.error("Failed to send verification email to: {}", user.getEmail());
                throw new RuntimeException("Failed to send verification email");
            }
            
        } catch (Exception e) {
            log.error("Error sending verification email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendWelcomeEmail(User user) {
        log.info("Sending welcome email to verified user: {}", user.getEmail());
        
        try {
            String welcomeSubject = "Welcome to Coremods - You're All Set!";
            String welcomeHtml = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #2563eb;">Welcome to Coremods, %s!</h2>
                        <p>Your email has been successfully verified and your account is now active.</p>
                        <p>You can now log in and start using all the features of Coremods.</p>
                        <p><a href="http://localhost:8080/auth/login" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px;">Log In Now</a></p>
                        <p>Thank you for joining us!</p>
                        <p>Best regards,<br>The Coremods Team</p>
                    </div>
                </body>
                </html>
                """, user.getUsername());
            
            String welcomeText = String.format("""
                Welcome to Coremods, %s!
                
                Your email has been successfully verified and your account is now active.
                
                You can now log in and start using all the features of Coremods.
                
                Login at: http://localhost:8080/auth/login
                
                Thank you for joining us!
                
                Best regards,
                The Coremods Team
                """, user.getUsername());
            
            boolean emailSent = resendEmailService.sendEmailWithPlainText(
                user.getEmail(),
                welcomeSubject,
                welcomeHtml,
                welcomeText
            );
            
            if (emailSent) {
                log.info("Welcome email sent successfully to: {}", user.getEmail());
            } else {
                log.warn("Failed to send welcome email to: {} (non-critical)", user.getEmail());
            }
            
        } catch (Exception e) {
            log.warn("Error sending welcome email to: {} (non-critical)", user.getEmail(), e);
        }
    }

    public void sendPasswordResetEmail(User user, String token) {
        log.info("Sending password reset email to: {}", user.getEmail());
        
        try {
            String resetSubject = "Password Reset Request - Coremods";
            String resetUrl = String.format("http://localhost:8080/auth/reset-password?token=%s", token);
            
            String resetHtml = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #dc2626;">Password Reset Request</h2>
                        <p>Hello %s,</p>
                        <p>We received a request to reset your password for your Coremods account.</p>
                        <p>Click the button below to reset your password. This link will expire in 5 minutes.</p>
                        <p><a href="%s" style="background-color: #dc2626; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px;">Reset Password</a></p>
                        <p>If you didn't request a password reset, please ignore this email. Your password will remain unchanged.</p>
                        <p>If the button doesn't work, copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; color: #6b7280;">%s</p>
                        <p>Best regards,<br>The Coremods Team</p>
                    </div>
                </body>
                </html>
                """, user.getUsername(), resetUrl, resetUrl);
            
            String resetText = String.format("""
                Password Reset Request
                
                Hello %s,
                
                We received a request to reset your password for your Coremods account.
                
                Click the link below to reset your password. This link will expire in 5 minutes.
                
                %s
                
                If you didn't request a password reset, please ignore this email. Your password will remain unchanged.
                
                Best regards,
                The Coremods Team
                """, user.getUsername(), resetUrl);
            
            boolean emailSent = resendEmailService.sendEmailWithPlainText(
                user.getEmail(),
                resetSubject,
                resetHtml,
                resetText
            );
            
            if (!emailSent) {
                log.error("Failed to send password reset email to: {}", user.getEmail());
                throw new RuntimeException("Failed to send password reset email");
            }
            
        } catch (Exception e) {
            log.error("Error sending password reset email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
} 