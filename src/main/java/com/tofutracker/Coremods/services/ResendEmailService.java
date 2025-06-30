package com.tofutracker.Coremods.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ResendEmailService {

    private final Resend resend;
    private final String fromEmail;

    public ResendEmailService(@Value("${resend.api.key}") String apiKey,
                              @Value("${app.email.from}") String fromEmail) {
        this.resend = new Resend(apiKey);
        this.fromEmail = fromEmail;
    }

    public boolean sendEmailWithPlainText(String toEmail, String subject, String htmlContent, String textContent) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(toEmail)
                    .subject(subject)
                    .html(htmlContent)
                    .text(textContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            
            log.info("Email with plain text sent successfully to: {} with ID: {}", toEmail, response.getId());
            return true;

        } catch (ResendException e) {
            log.error("Failed to send email with plain text to: {} - Error: {}", toEmail, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error sending email with plain text to: {}", toEmail, e);
            return false;
        }
    }
} 