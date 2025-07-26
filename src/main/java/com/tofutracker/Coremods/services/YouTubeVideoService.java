package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.dto.requests.mods.media.YouTubeVideoRequest;
import com.tofutracker.Coremods.dto.requests.mods.media.YouTubeVideoUpdateRequest;
import com.tofutracker.Coremods.dto.responses.mods.upload_mod.YouTubeVideoResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.entity.YouTubeVideo;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.YouTubeVideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeVideoService {

    private final YouTubeVideoRepository youTubeVideoRepository;
    private final GameModRepository gameModRepository;

    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
            "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[a-zA-Z0-9_-]{11}.*$"
    );

    @Transactional
    public List<YouTubeVideoResponse> addYouTubeVideos(Long gameModId, List<YouTubeVideoRequest> videoRequests, User currentUser) {
        validateModOwnership(gameModId, currentUser);

        GameMod gameMod = findGameModById(gameModId);
        
        for (YouTubeVideoRequest request : videoRequests) {
            validateYouTubeUrl(request.getYoutubeUrl());
            validateTitle(request.getTitle());
            
            String identifier = extractYouTubeVideoId(request.getYoutubeUrl());
            
            if (youTubeVideoRepository.existsByGameModIdAndIdentifier(gameModId, identifier)) {
                throw new BadRequestException("YouTube video already exists for this mod: " + identifier);
            }
        }
        
        List<YouTubeVideo> videosToSave = new ArrayList<>();
        
        for (YouTubeVideoRequest request : videoRequests) {
            String identifier = extractYouTubeVideoId(request.getYoutubeUrl());
            
            YouTubeVideo youTubeVideo = YouTubeVideo.builder()
                    .gameMod(gameMod)
                    .identifier(identifier)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .displayOrder(request.getDisplayOrder())
                    .build();
            
            videosToSave.add(youTubeVideo);
        }
        
        List<YouTubeVideo> savedVideos = youTubeVideoRepository.saveAll(videosToSave);
        log.info("Added {} YouTube videos for mod: {}", savedVideos.size(), gameModId);
        
        return YouTubeVideoResponse.from(savedVideos);
    }

    @Transactional(readOnly = true)
    public List<YouTubeVideoResponse> getYouTubeVideos(Long gameModId) {
        List<YouTubeVideo> videos = youTubeVideoRepository.findByGameModIdOrderByDisplayOrderAsc(gameModId);
        return YouTubeVideoResponse.from(videos);
    }

    @Transactional
    public YouTubeVideoResponse updateYouTubeVideo(Long gameModId, Long videoId, YouTubeVideoUpdateRequest request, User currentUser) {
        validateModOwnership(gameModId, currentUser);
        
        log.info("Updating YouTube video: {}, mod: {}, user: {}",
                videoId, gameModId, currentUser.getUsername());

        YouTubeVideo youTubeVideo = findYouTubeVideoById(videoId);
        
        if (!youTubeVideo.getGameMod().getId().equals(gameModId)) {
            throw new BadRequestException("Video does not belong to the specified mod");
        }
        
        validateTitle(request.getTitle());

        youTubeVideo.setTitle(request.getTitle());
        youTubeVideo.setDescription(request.getDescription());

        YouTubeVideo updatedVideo = youTubeVideoRepository.save(youTubeVideo);
        log.info("YouTube video updated successfully: {}", videoId);

        return YouTubeVideoResponse.from(updatedVideo);
    }

    @Transactional
    public void deleteYouTubeVideo(Long gameModId, Long videoId, User currentUser) {
        validateModOwnership(gameModId, currentUser);

        YouTubeVideo youTubeVideo = findYouTubeVideoById(videoId);
        
        if (!youTubeVideo.getGameMod().getId().equals(gameModId)) {
            throw new BadRequestException("Video does not belong to the specified mod");
        }
        
        youTubeVideoRepository.delete(youTubeVideo);
        log.info("YouTube video deleted successfully: {}", videoId);
    }

    private void validateModOwnership(Long gameModId, User currentUser) {
        GameMod gameMod = findGameModById(gameModId);
        
        if (!gameMod.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to modify this mod's YouTube videos");
        }
    }

    private void validateYouTubeUrl(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            throw new BadRequestException("YouTube URL is required");
        }

        if (!YOUTUBE_URL_PATTERN.matcher(youtubeUrl).matches()) {
            throw new BadRequestException("Invalid YouTube URL format");
        }
    }

    private String extractYouTubeVideoId(String youtubeUrl) {
        if (youtubeUrl.contains("youtube.com/watch") && youtubeUrl.contains("v=")) {
            Pattern pattern = Pattern.compile("v=([^&]+)");
            java.util.regex.Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        
        if (youtubeUrl.contains("youtu.be/")) {
            Pattern pattern = Pattern.compile("youtu\\.be/([^?&]+)");
            java.util.regex.Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        
        throw new BadRequestException("Unable to extract YouTube video ID from URL: " + youtubeUrl);
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new BadRequestException("Video title is required");
        }

        if (title.length() > 255) {
            throw new BadRequestException("Video title cannot exceed 255 characters");
        }
    }

    private GameMod findGameModById(Long gameModId) {
        return gameModRepository.findById(gameModId)
                .orElseThrow(() -> new ResourceNotFoundException("GameMod not found with id: " + gameModId));
    }

    private YouTubeVideo findYouTubeVideoById(Long videoId) {
        return youTubeVideoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("YouTube video not found with id: " + videoId));
    }
} 