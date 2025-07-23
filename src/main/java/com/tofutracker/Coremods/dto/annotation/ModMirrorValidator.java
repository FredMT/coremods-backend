package com.tofutracker.Coremods.dto.annotation;

import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModRequirementsMirrorsRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ModMirrorValidator implements ConstraintValidator<ValidModMirror, ModRequirementsMirrorsRequest.ModMirror> {

    @Override
    public boolean isValid(ModRequirementsMirrorsRequest.ModMirror value, ConstraintValidatorContext context) {
        if (value == null) return true;

        boolean hasName = value.getMirrorName() != null && !value.getMirrorName().isBlank();
        boolean hasUrl = value.getMirrorUrl() != null && !value.getMirrorUrl().isBlank();

        if (!hasName || !hasUrl) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Both mirrorName and mirrorUrl must be provided for each mod mirror")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

