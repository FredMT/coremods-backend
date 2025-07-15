package com.tofutracker.Coremods.services.images;

import com.tofutracker.Coremods.entity.Image;
import com.tofutracker.Coremods.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ImageValidationService {

    private static final long HEADER_IMAGE_MAX_SIZE = 750 * 1024;
    private static final long MOD_IMAGE_MAX_SIZE = 8 * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");

    public void validateImageFile(MultipartFile file, Image.ImageType imageType) {
        if (file.isEmpty()) {
            throw new BadRequestException("Image file cannot be empty");
        }

        validateFileSize(file, imageType);
        validateFileExtension(file);
        validateContentType(file);
    }

    private void validateFileSize(MultipartFile file, Image.ImageType imageType) {
        long maxSize = (imageType == Image.ImageType.HEADER) ? HEADER_IMAGE_MAX_SIZE : MOD_IMAGE_MAX_SIZE;
        if (file.getSize() > maxSize) {
            String maxSizeStr = (imageType == Image.ImageType.HEADER) ? "750KB" : "8MB";
            throw new BadRequestException("Image file size exceeds maximum allowed size of " + maxSizeStr);
        }
    }

    private void validateFileExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Invalid image format. Allowed formats: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File must be an image");
        }
    }
} 