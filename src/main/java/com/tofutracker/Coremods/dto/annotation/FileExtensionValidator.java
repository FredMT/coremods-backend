package com.tofutracker.Coremods.dto.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileExtensionValidator implements ConstraintValidator<ValidFileExtension, MultipartFile> {

    private List<String> extensions;

    @Override
    public void initialize(ValidFileExtension constraintAnnotation) {
        extensions = List.of(constraintAnnotation.extensions());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        var extension = FilenameUtils.getExtension(file.getOriginalFilename());
        return StringUtils.hasText(extension) && extensions.contains(extension.toLowerCase());
    }
} 