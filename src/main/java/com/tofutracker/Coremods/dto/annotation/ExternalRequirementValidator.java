package com.tofutracker.Coremods.dto.annotation;

import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModRequirementsMirrorsRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExternalRequirementValidator implements ConstraintValidator<ValidExternalRequirement, ModRequirementsMirrorsRequest.ExternalRequirement> {

    @Override
    public boolean isValid(ModRequirementsMirrorsRequest.ExternalRequirement value, ConstraintValidatorContext context) {
        if (value == null) return true;

        boolean hasName = value.getName() != null && !value.getName().isBlank();
        boolean hasUrl = value.getUrl() != null && !value.getUrl().isBlank();

        if (!hasName || !hasUrl) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Both name and url must be provided for each external requirement")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
