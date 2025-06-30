package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {
    
    private String passwordField;
    private String confirmPasswordField;
    private String message;
    
    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.passwordField = constraintAnnotation.password();
        this.confirmPasswordField = constraintAnnotation.confirmPassword();
        this.message = constraintAnnotation.message();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object passwordValue = new BeanWrapperImpl(value).getPropertyValue(passwordField);
        Object confirmPasswordValue = new BeanWrapperImpl(value).getPropertyValue(confirmPasswordField);
        
        boolean isValid = passwordValue != null && passwordValue.equals(confirmPasswordValue);
        
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                   .addPropertyNode(confirmPasswordField)
                   .addConstraintViolation();
        }
        
        return isValid;
    }
} 