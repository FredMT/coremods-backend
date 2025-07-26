package com.tofutracker.Coremods.dto.responses.mods.upload_mod;

import com.tofutracker.Coremods.entity.Image;
import com.tofutracker.Coremods.services.images.ImageStorageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {
    private Long imageId;
    private String imageUrl;
    private String imageName;
    private String imageType;
    private Long fileSize;
    
    public static ImageUploadResponse from(Image image, ImageStorageService imageStorageService) {
        return new ImageUploadResponse(
                image.getId(),
                imageStorageService.getImageUrl(image),
                image.getStorageKey(),
                image.getImageType().toString(),
                image.getFileSize()
        );
    }
} 