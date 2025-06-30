package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    
    private static final Pattern HAS_UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern HAS_NUMBER = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL_CHAR = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");
    
    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true; // Let @NotBlank handle null validation
        }
        
        boolean hasUppercase = HAS_UPPERCASE.matcher(password).find();
        boolean hasLowercase = HAS_LOWERCASE.matcher(password).find();
        boolean hasNumber = HAS_NUMBER.matcher(password).find();
        boolean hasSpecialChar = HAS_SPECIAL_CHAR.matcher(password).find();
        
        boolean isValid = hasUppercase && hasLowercase && hasNumber && hasSpecialChar;
        
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            
            StringBuilder message = new StringBuilder("Password must contain ");
            if (!hasUppercase) message.append("at least one uppercase letter, ");
            if (!hasLowercase) message.append("at least one lowercase letter, ");
            if (!hasNumber) message.append("at least one number, ");
            if (!hasSpecialChar) message.append("at least one special character, ");
            
            // Remove trailing comma and space
            String finalMessage = message.substring(0, message.length() - 2);
            
            context.buildConstraintViolationWithTemplate(finalMessage)
                   .addConstraintViolation();
        }
        
        return isValid;
    }
} 