package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailTemplateService {

    @Value("${app.email.verification.url}")
    private String verificationBaseUrl;

    public String loadEmailTemplate(String templateName, Map<String, String> placeholders) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email/" + templateName);
            String template = resource.getContentAsString(StandardCharsets.UTF_8);
            
            // Replace placeholders
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            
            return template;
        } catch (IOException e) {
            log.error("Failed to load email template: {}", templateName, e);
            return getDefaultTemplate(placeholders);
        }
    }

    public EmailContent generateVerificationEmail(User user, String token) {
        String verificationUrl = verificationBaseUrl + "?token=" + token;
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("verificationUrl", verificationUrl);
        placeholders.put("username", user.getUsername());
        placeholders.put("email", user.getEmail());

        String htmlContent = loadEmailTemplate("verification-email.html", placeholders);
        String textContent = loadEmailTemplate("verification-email.txt", placeholders);

        return new EmailContent(
                "Verify Your Email - Coremods",
                htmlContent,
                textContent
        );
    }

    private String getDefaultTemplate(Map<String, String> placeholders) {
        // Fallback template in case file loading fails
        String verificationUrl = placeholders.getOrDefault("verificationUrl", "#");
        
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2563eb;">Coremods - Email Verification</h2>
                    <p>Hello %s,</p>
                    <p>Please verify your email address by clicking the link below:</p>
                    <p><a href="%s" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px;">Verify Email</a></p>
                    <p>If the button doesn't work, copy and paste this URL: %s</p>
                    <p>This link expires in 24 hours.</p>
                    <p>Best regards,<br>The Coremods Team</p>
                </div>
            </body>
            </html>
            """, verificationUrl, verificationUrl);
    }

    public static class EmailContent {
        private final String subject;
        private final String htmlContent;
        private final String textContent;

        public EmailContent(String subject, String htmlContent, String textContent) {
            this.subject = subject;
            this.htmlContent = htmlContent;
            this.textContent = textContent;
        }

        public String getSubject() { return subject; }
        public String getHtmlContent() { return htmlContent; }
        public String getTextContent() { return textContent; }
    }
} 