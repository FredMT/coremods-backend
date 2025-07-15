package com.tofutracker.Coremods.services.images;

import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageValidationService imageValidationService;
    private final ImageStorageService imageStorageService;
    private final ImageDatabaseService imageDatabaseService;
    private final EntityManager entityManager;

    @Transactional
    public Image saveHeaderImage(Long gameModId, MultipartFile multipartFile) throws IOException {
        GameMod gameMod = imageDatabaseService.findGameModById(gameModId);
        imageValidationService.validateImageFile(multipartFile, Image.ImageType.HEADER);

        Optional<Image> existingHeader = imageDatabaseService.findHeaderImage(gameModId);
        if (existingHeader.isPresent()) {
            try {
                deleteImageFile(existingHeader.get());
                // Flush the deletion to ensure it's processed before inserting the new image
                entityManager.flush();
            } catch (Exception e) {
                log.error("Failed to delete existing header image: {}", existingHeader.get().getName(), e);
                throw new RuntimeException("Failed to delete existing header image", e);
            }
        }

        return saveImageFile(multipartFile, gameMod, Image.ImageType.HEADER, null);
    }

    @Transactional
    public Image saveModImage(Long gameModId, MultipartFile multipartFile) throws IOException {
        GameMod gameMod = imageDatabaseService.findGameModById(gameModId);
        imageValidationService.validateImageFile(multipartFile, Image.ImageType.MOD_IMAGE);

        long existingCount = imageDatabaseService.countModImages(gameModId);
        int displayOrder = (int) (existingCount + 1);

        return saveImageFile(multipartFile, gameMod, Image.ImageType.MOD_IMAGE, displayOrder);
    }

    @Transactional
    public void deleteImage(Long imageId) throws Exception {
        Image image = imageDatabaseService.findImageById(imageId);
        deleteImageFile(image);
    }

    @Transactional
    public void deleteImagesByGameMod(Long gameModId) {
        List<Image> images = imageDatabaseService.findImagesByGameMod(gameModId);

        for (Image image : images) {
            try {
                imageStorageService.deleteImage(image);
            } catch (Exception e) {
                log.error("Failed to delete image from S3: {}", image.getName(), e);
            }
        }

        imageDatabaseService.deleteImagesByGameMod(gameModId);
    }

    public List<Image> getImagesByGameMod(Long gameModId) {
        return imageDatabaseService.findImagesByGameMod(gameModId);
    }

    public Optional<Image> getHeaderImage(Long gameModId) {
        return imageDatabaseService.findHeaderImage(gameModId);
    }

    public List<Image> getModImages(Long gameModId) {
        return imageDatabaseService.findModImages(gameModId);
    }

    public String getImageUrl(Image image) {
        return imageStorageService.getImageUrl(image);
    }

    @Transactional
    public Image updateImageName(Long imageId, String newName) {
        Image image = imageDatabaseService.findImageById(imageId);
        image.setName(newName);
        return imageDatabaseService.saveImage(image);
    }

        private Image saveImageFile(MultipartFile multipartFile, GameMod gameMod, 
                               Image.ImageType imageType, Integer displayOrder) throws IOException {
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String uniqueImageName = imageStorageService.generateUniqueImageName(multipartFile.getOriginalFilename());

        imageStorageService.uploadImage(multipartFile, uniqueImageName);

        Image image = imageDatabaseService.createImage(gameMod, uniqueImageName, extension,
                imageType, multipartFile.getSize(), displayOrder);

        return imageDatabaseService.saveImage(image);
    }

    private void deleteImageFile(Image image) throws Exception {
        imageStorageService.deleteImage(image);
        imageDatabaseService.deleteImage(image);
        log.info("Image deleted successfully: {}", image.getName());
    }
} 