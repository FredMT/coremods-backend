package com.tofutracker.Coremods.dto.requests;

import org.springframework.web.multipart.MultipartFile;

import com.tofutracker.Coremods.dto.annotation.ValidFileExtension;
import com.tofutracker.Coremods.dto.annotation.ValidFileMaxSize;
import com.tofutracker.Coremods.dto.annotation.ValidFileMimeType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserImageUploadRequest {

    @NotNull(message = "Image file is required")
    @ValidFileMaxSize(maxSize = 3 * 1024 * 1024, message = "Image file size must not exceed 3MB")
    @ValidFileExtension(extensions = {"jpg", "jpeg", "png", "webp"}, message = "Only JPG, JPEG, PNG, and WEBP files are allowed")
    @ValidFileMimeType(mimeTypes = {"image/jpeg", "image/png", "image/webp"}, 
            message = "Invalid file format. Only JPG, JPEG, PNG, and WEBP files are allowed")
    private MultipartFile file;
}