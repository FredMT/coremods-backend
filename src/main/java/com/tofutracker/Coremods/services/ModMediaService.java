package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.dto.requests.ImageUpdateRequest;
import com.tofutracker.Coremods.dto.requests.YouTubeVideoRequest;
import com.tofutracker.Coremods.dto.requests.YouTubeVideoUpdateRequest;
import com.tofutracker.Coremods.dto.responses.ImageResponse;
import com.tofutracker.Coremods.dto.responses.ImageUploadResponse;
import com.tofutracker.Coremods.dto.responses.YouTubeVideoResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.Image;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.entity.YouTubeVideo;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.services.images.ImageService;
import com.tofutracker.Coremods.services.images.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModMediaService {
    
    private final ImageService imageService;
    private final YouTubeVideoService youTubeVideoService;
    private final ImageStorageService imageStorageService;
    private final GameModRepository gameModRepository;
    
    @Transactional
    public ImageUploadResponse uploadHeaderImage(Long gameModId, MultipartFile file, User currentUser) throws IOException {
        validateModOwnership(gameModId, currentUser);
        
        log.info("Uploading header image for mod: {}, user: {}", gameModId, currentUser.getUsername());
        
        Image image = imageService.saveHeaderImage(gameModId, file);
        return ImageUploadResponse.from(image, imageStorageService);
    }
    
    @Transactional
    public ImageUploadResponse uploadModImage(Long gameModId, MultipartFile file, User currentUser) throws IOException {
        validateModOwnership(gameModId, currentUser);
        
        log.info("Uploading mod image for mod: {}, user: {}", gameModId, currentUser.getUsername());
        
        Image image = imageService.saveModImage(gameModId, file);
        return ImageUploadResponse.from(image, imageStorageService);
    }
    
    @Transactional(readOnly = true)
    public List<ImageResponse> getImages(Long gameModId) {
        List<Image> images = imageService.getImagesByGameMod(gameModId);
        return ImageResponse.from(images, imageStorageService);
    }
    
    @Transactional(readOnly = true)
    public ImageResponse getHeaderImage(Long gameModId) {
        Image headerImage = imageService.getHeaderImage(gameModId).orElse(null);
        return headerImage != null ? ImageResponse.from(headerImage, imageStorageService) : null;
    }
    
    @Transactional(readOnly = true)
    public List<ImageResponse> getModImages(Long gameModId) {
        List<Image> modImages = imageService.getModImages(gameModId);
        return ImageResponse.from(modImages, imageStorageService);
    }
    
    @Transactional
    public void deleteImage(Long gameModId, Long imageId, User currentUser) throws Exception {
        validateModOwnership(gameModId, currentUser);
        
        log.info("Deleting image: {}, mod: {}, user: {}", imageId, gameModId, currentUser.getUsername());
        
        imageService.deleteImage(imageId);
    }
    
    @Transactional
    public ImageResponse updateImageName(Long gameModId, Long imageId, ImageUpdateRequest request, User currentUser) {
        validateModOwnership(gameModId, currentUser);
        
        log.info("Updating image name: {}, mod: {}, user: {}, new name: {}", 
                imageId, gameModId, currentUser.getUsername(), request.getName());
        
        Image updatedImage = imageService.updateImageName(imageId, request.getName());
        return ImageResponse.from(updatedImage, imageStorageService);
    }
    
    @Transactional
    public YouTubeVideoResponse addYouTubeVideo(Long gameModId, YouTubeVideoRequest request, User currentUser) {
        validateModOwnership(gameModId, currentUser);
        
        log.info("Adding YouTube video for mod: {}, user: {}, URL: {}",
                gameModId, currentUser.getUsername(), request.getYoutubeUrl());
        
        YouTubeVideo video = youTubeVideoService.addYouTubeVideo(
                gameModId,
                request.getYoutubeUrl(),
                request.getTitle(),
                request.getDescription()
        );
        
        return YouTubeVideoResponse.from(video);
    }
    
    @Transactional(readOnly = true)
    public List<YouTubeVideoResponse> getYouTubeVideos(Long gameModId) {
        List<YouTubeVideo> videos = youTubeVideoService.getYouTubeVideosByGameMod(gameModId);
        return YouTubeVideoResponse.from(videos);
    }
    
    @Transactional
    public YouTubeVideoResponse updateYouTubeVideo(Long gameModId, Long videoId, YouTubeVideoUpdateRequest request, User currentUser) {
        validateModOwnership(gameModId, currentUser);
        
        log.info("Updating YouTube video: {}, mod: {}, user: {}",
                videoId, gameModId, currentUser.getUsername());
        
        YouTubeVideo video = youTubeVideoService.updateYouTubeVideo(
                videoId,
                request.getTitle(),
                request.getDescription()
        );
        
        return YouTubeVideoResponse.from(video);
    }
    
    @Transactional
    public void deleteYouTubeVideo(Long gameModId, Long videoId, User currentUser) {
        validateModOwnership(gameModId, currentUser);
        
        log.info("Deleting YouTube video: {}, mod: {}, user: {}",
                videoId, gameModId, currentUser.getUsername());
        
        youTubeVideoService.deleteYouTubeVideo(videoId);
    }
    
    private void validateModOwnership(Long gameModId, User currentUser) {
        GameMod gameMod = gameModRepository.findById(gameModId)
                .orElseThrow(() -> new ResourceNotFoundException("GameMod not found with id: " + gameModId));
        
        if (!gameMod.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to modify this mod's media");
        }
    }
} 