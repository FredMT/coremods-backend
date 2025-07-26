package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.mods.media.YouTubeVideoRequest;
import com.tofutracker.Coremods.dto.requests.mods.media.YouTubeVideoUpdateRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.upload_mod.YouTubeVideoResponse;
import com.tofutracker.Coremods.entity.Image;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.images.ImageService;
import com.tofutracker.Coremods.services.YouTubeVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/mods/{gameModId}")
@RequiredArgsConstructor
@Slf4j
public class ModMediaController {

    private final ImageService imageService;
    private final YouTubeVideoService youTubeVideoService;

    @PostMapping("/images/header")
    public ResponseEntity<ApiResponse<Image>> uploadHeaderImage(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Header image file") @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) throws IOException {

        Image image = imageService.saveHeaderImage(gameModId, file, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Header image uploaded successfully", image));
    }

    @PostMapping("/images/mod")
    public ResponseEntity<ApiResponse<Image>> uploadModImage(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Mod image file") @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) throws IOException {

        Image image = imageService.saveModImage(gameModId, file, currentUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Mod image uploaded successfully", image));
    }

    @GetMapping("/images")
    @Operation(summary = "Get all images for mod", description = "Retrieve all images associated with a mod")
    public ResponseEntity<ApiResponse<List<Image>>> getImages(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId) {

        List<Image> images = imageService.getImagesByGameMod(gameModId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Images retrieved successfully", images));
    }

    // TODO: Return proper response
    @GetMapping("/images/header")
    @Operation(summary = "Get header image for mod", description = "Retrieve the header image for a mod")
    public ResponseEntity<ApiResponse<Image>> getHeaderImage(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId) {

        Optional<Image> image = imageService.getHeaderImage(gameModId);

        if (image.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No header image found"));
        }

        return ResponseEntity.ok(ApiResponse.success("Image retrieved successfully", image.get()));

    }

    @GetMapping("/images/mod")
    @Operation(summary = "Get mod images", description = "Retrieve all mod images (excluding header)")
    public ResponseEntity<ApiResponse<List<Image>>> getModImages(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId) {

        List<Image> images = imageService.getModImages(gameModId);

        if (images.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No header image found"));
        }

        return ResponseEntity.ok(ApiResponse.success("Mod images retrieved successfully", images));
    }

    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "Delete image", description = "Delete a specific image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Image ID") @PathVariable Long imageId,
            @AuthenticationPrincipal User currentUser) throws Exception {

        imageService.deleteImage(gameModId, imageId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully"));
    }

    @PostMapping("/youtube-videos")
    @Operation(summary = "Add YouTube videos", description = "Add one or more YouTube video links to the mod")
    public ResponseEntity<ApiResponse<List<YouTubeVideoResponse>>> addYouTubeVideos(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Valid @RequestBody List<YouTubeVideoRequest> requests,
            @AuthenticationPrincipal User currentUser) {

        List<YouTubeVideoResponse> videoResponses = youTubeVideoService.addYouTubeVideos(gameModId, requests,
                currentUser);

        return ResponseEntity.ok(ApiResponse.success("YouTube videos added successfully", videoResponses));
    }

    @GetMapping("/youtube-videos")
    @Operation(summary = "Get YouTube videos", description = "Retrieve all YouTube videos for a mod")
    public ResponseEntity<ApiResponse<List<YouTubeVideoResponse>>> getYouTubeVideos(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId) {

        List<YouTubeVideoResponse> videoResponses = youTubeVideoService.getYouTubeVideos(gameModId);

        if  (videoResponses.isEmpty()) {
            return  ResponseEntity.ok(ApiResponse.success("No videos found"));
        }

        return ResponseEntity.ok(ApiResponse.success("YouTube videos retrieved successfully", videoResponses));
    }

    @PutMapping("/youtube-videos/{videoId}")
    @Operation(summary = "Update YouTube video", description = "Update YouTube video title and description")
    public ResponseEntity<ApiResponse<YouTubeVideoResponse>> updateYouTubeVideo(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Video ID") @PathVariable Long videoId,
            @Valid @RequestBody YouTubeVideoUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {

        YouTubeVideoResponse videoResponse = youTubeVideoService.updateYouTubeVideo(gameModId, videoId, request,
                currentUser);
        return ResponseEntity.ok(ApiResponse.success("YouTube video updated successfully", videoResponse));
    }

    @DeleteMapping("/youtube-videos/{videoId}")
    @Operation(summary = "Delete YouTube video", description = "Delete a YouTube video link")
    public ResponseEntity<ApiResponse<Void>> deleteYouTubeVideo(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Video ID") @PathVariable Long videoId,
            @AuthenticationPrincipal User currentUser) {

        youTubeVideoService.deleteYouTubeVideo(gameModId, videoId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("YouTube video deleted successfully"));
    }
}