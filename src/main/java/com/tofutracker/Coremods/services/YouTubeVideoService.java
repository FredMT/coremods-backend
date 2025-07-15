package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.entity.YouTubeVideo;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.YouTubeVideoRepository;
import com.tofutracker.Coremods.repository.GameModRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeVideoService {

    private final YouTubeVideoRepository youTubeVideoRepository;
    private final GameModRepository gameModRepository;

    // YouTube URL validation patterns
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
            "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[a-zA-Z0-9_-]{11}.*$"
    );

    @Transactional
    public YouTubeVideo addYouTubeVideo(Long gameModId, String youtubeUrl, String title, String description) {
        GameMod gameMod = findGameModById(gameModId);
        validateYouTubeUrl(youtubeUrl);
        validateTitle(title);

        if (youTubeVideoRepository.existsByGameModIdAndYoutubeUrl(gameModId, youtubeUrl)) {
            throw new BadRequestException("YouTube video URL already exists for this mod");
        }

        long existingCount = youTubeVideoRepository.countByGameModId(gameModId);
        int displayOrder = (int) (existingCount + 1);

        YouTubeVideo youTubeVideo = YouTubeVideo.builder()
                .gameMod(gameMod)
                .youtubeUrl(youtubeUrl)
                .title(title)
                .description(description)
                .displayOrder(displayOrder)
                .build();

        YouTubeVideo savedVideo = youTubeVideoRepository.save(youTubeVideo);
        log.info("YouTube video added successfully for mod: {}, URL: {}", gameModId, youtubeUrl);

        return savedVideo;
    }

    @Transactional
    public YouTubeVideo updateYouTubeVideo(Long videoId, String title, String description) {
        YouTubeVideo youTubeVideo = findYouTubeVideoById(videoId);
        validateTitle(title);

        youTubeVideo.setTitle(title);
        youTubeVideo.setDescription(description);

        YouTubeVideo updatedVideo = youTubeVideoRepository.save(youTubeVideo);
        log.info("YouTube video updated successfully: {}", videoId);

        return updatedVideo;
    }

    @Transactional
    public void deleteYouTubeVideo(Long videoId) {
        YouTubeVideo youTubeVideo = findYouTubeVideoById(videoId);
        youTubeVideoRepository.delete(youTubeVideo);
        log.info("YouTube video deleted successfully: {}", videoId);
    }

    @Transactional
    public void deleteYouTubeVideosByGameMod(Long gameModId) {
        youTubeVideoRepository.deleteByGameModId(gameModId);
        log.info("All YouTube videos deleted for mod: {}", gameModId);
    }

    public List<YouTubeVideo> getYouTubeVideosByGameMod(Long gameModId) {
        return youTubeVideoRepository.findByGameModIdOrderByDisplayOrderAsc(gameModId);
    }

    public YouTubeVideo getYouTubeVideo(Long videoId) {
        return findYouTubeVideoById(videoId);
    }

    public long countYouTubeVideosByGameMod(Long gameModId) {
        return youTubeVideoRepository.countByGameModId(gameModId);
    }

    private void validateYouTubeUrl(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            throw new BadRequestException("YouTube URL is required");
        }

        if (!YOUTUBE_URL_PATTERN.matcher(youtubeUrl).matches()) {
            throw new BadRequestException("Invalid YouTube URL format");
        }
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