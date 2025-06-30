package com.tofutracker.Coremods.dto.requests;

import com.tofutracker.Coremods.dto.annotation.PasswordMatch;
import com.tofutracker.Coremods.dto.annotation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatch(password = "newPassword", confirmPassword = "confirmNewPassword", message = "Passwords do not match")
public class ForgotPasswordResetRequest {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @StrongPassword
    private String newPassword;

    @NotBlank(message = "Confirm new password is required")
    private String confirmNewPassword;
} 