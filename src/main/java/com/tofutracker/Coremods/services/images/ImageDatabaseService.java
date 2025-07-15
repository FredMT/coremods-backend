package com.tofutracker.Coremods.services.images;

import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.Image;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageDatabaseService {

    private final ImageRepository imageRepository;
    private final GameModRepository gameModRepository;

    @Transactional
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Image image) {
        imageRepository.delete(image);
        log.info("Image deleted from database: {}", image.getName());
    }

    @Transactional
    public void deleteImagesByGameMod(Long gameModId) {
        imageRepository.deleteByGameModId(gameModId);
        log.info("All images deleted for mod: {}", gameModId);
    }

    public Image findImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));
    }

    public List<Image> findImagesByGameMod(Long gameModId) {
        return imageRepository.findByGameModIdOrderByDisplayOrderAsc(gameModId);
    }

    public Optional<Image> findHeaderImage(Long gameModId) {
        return imageRepository.findHeaderImageByGameModId(gameModId);
    }

    public List<Image> findModImages(Long gameModId) {
        return imageRepository.findModImagesByGameModId(gameModId);
    }

    public long countModImages(Long gameModId) {
        return imageRepository.countModImagesByGameModId(gameModId);
    }

    public GameMod findGameModById(Long gameModId) {
        return gameModRepository.findById(gameModId)
                .orElseThrow(() -> new ResourceNotFoundException("GameMod not found with id: " + gameModId));
    }

        public Image createImage(GameMod gameMod, String uniqueImageName, String extension, 
                           Image.ImageType imageType, long fileSize, Integer displayOrder) {
        return Image.builder()
                .gameMod(gameMod)
                .name(uniqueImageName)
                .ext(extension)
                .imageType(imageType)
                .fileSize(fileSize)
                .displayOrder(imageType == Image.ImageType.MOD_IMAGE ? displayOrder : null)
                .build();
    }
} 