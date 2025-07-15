package com.tofutracker.Coremods.dto.responses;

import com.tofutracker.Coremods.entity.Image;
import com.tofutracker.Coremods.services.images.ImageStorageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private Long id;
    private String name;
    private String imageType;
    private String imageUrl;
    private Integer displayOrder;
    private LocalDateTime createdTime;
    
    public static ImageResponse from(Image image, ImageStorageService imageStorageService) {
        return new ImageResponse(
            image.getId(),
            image.getName(),
            image.getImageType().toString(),
            imageStorageService.getImageUrl(image),
            image.getDisplayOrder(),
            image.getCreatedTime()
        );
    }
    
    public static List<ImageResponse> from(List<Image> images, ImageStorageService imageStorageService) {
        return images.stream()
                .map(image -> ImageResponse.from(image, imageStorageService))
                .collect(Collectors.toList());
    }
} 