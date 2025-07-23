package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExternalRequirementValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidExternalRequirement {
    String message() default "Both name and url are required if external requirement is provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
