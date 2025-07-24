package com.tofutracker.Coremods.dto.annotation;

import com.tofutracker.Coremods.dto.requests.mods.upload_files.ModFileUploadRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileVersionValidator implements ConstraintValidator<ValidFileVersion, ModFileUploadRequest> {

    private static final String FILE_ID_ERROR =
            "When uploading a new version of an existing file, fileId is required and must be positive";
    private static final String REMOVE_PREV_ERROR =
            "When uploading a new version of an existing file, removePreviousFileVersion must be specified";

    @Override
    public boolean isValid(ModFileUploadRequest request, ConstraintValidatorContext context) {
        if (request == null || !Boolean.TRUE.equals(request.getIsNewVersionOfExistingFile())) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (request.getFileId() == null || request.getFileId() <= 0) {
            context.buildConstraintViolationWithTemplate(FILE_ID_ERROR)
                    .addPropertyNode("fileId")
                    .addConstraintViolation();
            valid = false;
        }

        if (request.getRemovePreviousFileVersion() == null) {
            context.buildConstraintViolationWithTemplate(REMOVE_PREV_ERROR)
                    .addPropertyNode("removePreviousFileVersion")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
