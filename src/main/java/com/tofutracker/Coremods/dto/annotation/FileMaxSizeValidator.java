package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileMaxSizeValidator implements ConstraintValidator<ValidFileMaxSize, MultipartFile> {

    private long maxSizeInBytes;

    @Override
    public void initialize(ValidFileMaxSize constraintAnnotation) {
        maxSizeInBytes = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        return file == null || file.isEmpty() || file.getSize() <= maxSizeInBytes;
    }
} 