package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {FileMimeTypeValidator.class}
)
public @interface ValidFileMimeType {
    String[] mimeTypes() default {};

    String message() default "{constraints.ValidFileMimeType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
} 