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
                emailContent.getSubject(),
                emailContent.getHtmlContent(),
                emailContent.getTextContent()
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
} 