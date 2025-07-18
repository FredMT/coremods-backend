package com.tofutracker.Coremods.dto.requests.auth;

import com.tofutracker.Coremods.dto.annotation.PasswordMatch;
import com.tofutracker.Coremods.dto.annotation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatch(password = "newPassword", confirmPassword = "confirmNewPassword", message = "New passwords do not match")
public class ResetPasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    @StrongPassword
    private String newPassword;

    @NotBlank(message = "Confirm new password is required")
    private String confirmNewPassword;
} 