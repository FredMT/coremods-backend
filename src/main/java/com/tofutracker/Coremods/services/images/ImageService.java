package com.tofutracker.Coremods.services.images;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tofutracker.Coremods.config.enums.ModImageType;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.Image;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ImageRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageStorageService imageStorageService;
    private final ImageRepository imageRepository;
    private final GameModRepository gameModRepository;
    private final EntityManager entityManager;

    private static final long HEADER_IMAGE_MAX_SIZE = 750 * 1024;
    private static final long MOD_IMAGE_MAX_SIZE = 8 * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    @Transactional
    public Image saveHeaderImage(Long gameModId, MultipartFile multipartFile, User currentUser) throws IOException {
        validateModOwnership(gameModId, currentUser);
        validateImageFile(multipartFile, ModImageType.HEADER);

        Optional<Image> existingHeader = findHeaderImage(gameModId);
        if (existingHeader.isPresent()) {
            try {
                deleteImageFile(existingHeader.get());
                entityManager.flush();
            } catch (Exception e) {
                log.error("Failed to delete existing header image for mod: {}", gameModId, e);
                throw new RuntimeException("Failed to delete existing header image", e);
            }
        }

        log.info("Saving header image for mod: {}, user: {}", gameModId, currentUser.getUsername());
        return saveImageFile(multipartFile, gameModId, ModImageType.HEADER, null);
    }

    @Transactional
    public Image saveModImage(Long gameModId, MultipartFile multipartFile, User currentUser) throws IOException {
        validateModOwnership(gameModId, currentUser);
        validateImageFile(multipartFile, ModImageType.MOD_IMAGE);

        long existingCount = imageRepository
                .countByImageableTypeAndImageableIdAndImageType("MOD", gameModId, ModImageType.MOD_IMAGE)
                .orElse(0L);

        int displayOrder = (int) (existingCount + 1);

        log.info("Saving mod image for mod: {}, user: {}", gameModId, currentUser.getUsername());
        return saveImageFile(multipartFile, gameModId, ModImageType.MOD_IMAGE, displayOrder);
    }

    @Transactional
    public void deleteImage(Long gameModId, Long imageId, User currentUser) {
        validateModOwnership(gameModId, currentUser);

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        log.info("Deleting image: {}, mod: {}, user: {}", imageId, image.getImageableId(), currentUser.getUsername());
        deleteImageFile(image);
    }

    public List<Image> getImagesByGameMod(Long gameModId) {
        return imageRepository.findByImageableTypeAndImageableIdOrderByDisplayOrderAsc("MOD", gameModId)
                .orElse(List.of());
    }

    public Optional<Image> getHeaderImage(Long gameModId) {
        return findHeaderImage(gameModId);
    }

    public List<Image> getModImages(Long gameModId) {
        return imageRepository.findByImageableTypeAndImageableIdAndImageType("MOD", gameModId, ModImageType.MOD_IMAGE)
                .orElse(List.of());
    }

    private Image saveImageFile(MultipartFile multipartFile, Long gameModId,
            ModImageType imageType, Integer displayOrder) throws IOException {
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String baseFileName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
        String sanitizedFileName = sanitizeFileName(baseFileName);
        String uniqueFileName = sanitizedFileName + "_" + UUID.randomUUID().toString().replace("-", "");
        String storageKey = String.format("mods/%d/images/%s.%s", gameModId, uniqueFileName, extension);

        if (storageKey.startsWith(".") || storageKey.contains("..")) {
            throw new IllegalArgumentException("Invalid storage key: keys cannot start with '.' or contain '..'");
        }

        imageStorageService.uploadImage(multipartFile, storageKey);

        Image image = Image.builder()
                .imageableType("MOD")
                .imageableId(gameModId)
                .storageKey(storageKey)
                .imageType(imageType)
                .fileSize(multipartFile.getSize())
                .displayOrder(imageType == ModImageType.MOD_IMAGE ? displayOrder : null)
                .build();

        return imageRepository.save(image);
    }

    private void deleteImageFile(Image image) {
        imageStorageService.deleteImage(image.getStorageKey());

        Integer deletedDisplayOrder = image.getDisplayOrder();
        imageRepository.delete(image);

        if (deletedDisplayOrder != null) {
            imageRepository.shiftDisplayOrderDown(image.getImageableType(), image.getImageableId(),
                    deletedDisplayOrder);
        }

        log.info("Image deleted successfully: {}", image.getStorageKey());
    }

    private void validateImageFile(MultipartFile file, ModImageType imageType) {
        if (file.isEmpty()) {
            throw new BadRequestException("Image file cannot be empty");
        }

        validateFileSize(file, imageType);
        validateFileExtension(file);
        validateContentType(file);
    }

    private void validateFileSize(MultipartFile file, ModImageType imageType) {
        long maxSize = (imageType == ModImageType.HEADER) ? HEADER_IMAGE_MAX_SIZE : MOD_IMAGE_MAX_SIZE;
        if (file.getSize() > maxSize) {
            String maxSizeStr = (imageType == ModImageType.HEADER) ? "750KB" : "8MB";
            throw new BadRequestException("Image file size exceeds maximum allowed size of " + maxSizeStr);
        }
    }

    private void validateFileExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException(
                    "Invalid image format. Allowed formats: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File must be an image");
        }
    }

    private Optional<Image> findHeaderImage(Long gameModId) {
        return imageRepository.findFirstByImageableTypeAndImageableIdAndImageType("MOD", gameModId,
                ModImageType.HEADER);
    }

    private void validateModOwnership(Long gameModId, User currentUser) {
        GameMod gameMod = gameModRepository.findById(gameModId)
                .orElseThrow(() -> new ResourceNotFoundException("GameMod not found with id: " + gameModId));

        if (!gameMod.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to modify this mod's images");
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "image";
        }

        return fileName
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_{2,}", "_")
                .replaceAll("^[._]+", "")
                .replaceAll("[._]+$", "");
    }
}