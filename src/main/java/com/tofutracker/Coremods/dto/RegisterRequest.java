package com.tofutracker.Coremods.dto;

import com.tofutracker.Coremods.dto.annotation.PasswordMatch;
import com.tofutracker.Coremods.dto.annotation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatch(password = "password", confirmPassword = "confirmPassword", message = "Passwords do not match")
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 12, message = "Username must be between 4 and 12 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @StrongPassword
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
} 