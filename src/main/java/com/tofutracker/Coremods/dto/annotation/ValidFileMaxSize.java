package com.tofutracker.Coremods.dto.annotation;

import com.tofutracker.Coremods.services.ArchiveConstants;
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
        validatedBy = {FileMaxSizeValidator.class}
)
public @interface ValidFileMaxSize {
    long maxSize() default ArchiveConstants.MAX_TOTAL_SIZE; // Bytes

    String message() default "{constraints.ValidFileMaxSize.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
} 