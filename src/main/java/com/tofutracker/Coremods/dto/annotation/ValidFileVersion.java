package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FileVersionValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileVersion {
    String message() default "When uploading a new version of an existing file, fileId is required and must be positive";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 