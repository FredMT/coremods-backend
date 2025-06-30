package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 