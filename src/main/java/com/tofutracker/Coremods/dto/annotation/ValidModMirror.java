package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ModMirrorValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidModMirror {
    String message() default "Both mirrorName and mirrorUrl are required if mod mirror is provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

