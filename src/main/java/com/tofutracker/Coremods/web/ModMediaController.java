package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.mods.media.ImageUpdateRequest;
import com.tofutracker.Coremods.dto.requests.mods.media.YouTubeVideoRequest;
import com.tofutracker.Coremods.dto.requests.mods.media.YouTubeVideoUpdateRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.upload_mod.ImageResponse;
import com.tofutracker.Coremods.dto.responses.mods.upload_mod.ImageUploadResponse;
import com.tofutracker.Coremods.dto.responses.mods.upload_mod.YouTubeVideoResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.ModMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mods/{gameModId}")
@RequiredArgsConstructor
@Slf4j
public class ModMediaController {

    private final ModMediaService modMediaService;

    @PostMapping("/images/header")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadHeaderImage(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Header image file") @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) throws IOException {

        ImageUploadResponse uploadResponse = modMediaService.uploadHeaderImage(gameModId, file, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Header image uploaded successfully", uploadResponse));
    }

    @PostMapping("/images/mod")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadModImage(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Mod image file") @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) throws IOException {

        ImageUploadResponse uploadResponse = modMediaService.uploadModImage(gameModId, file, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Mod image uploaded successfully", uploadResponse));
    }

    @GetMapping("/images")
    @Operation(summary = "Get all images for mod", description = "Retrieve all images associated with a mod")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> getImages(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId) {

        List<ImageResponse> imageResponses = modMediaService.getImages(gameModId);
        return ResponseEntity.ok(ApiResponse.success("Images retrieved successfully", imageResponses));
    }

    @GetMapping("/images/header")
    @Operation(summary = "Get header image for mod", description = "Retrieve the header image for a mod")
    public ResponseEntity<ApiResponse<ImageResponse>> getHeaderImage(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId) {

        ImageResponse imageResponse = modMediaService.getHeaderImage(gameModId);
        
        ApiResponse<ImageResponse> response = imageResponse != null ?
                ApiResponse.success("Header image retrieved successfully", imageResponse) :
                ApiResponse.success("No header image found", null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/images/mod")
    @Operation(summary = "Get mod images", description = "Retrieve all mod images (excluding header)")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> getModImages(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId) {

        List<ImageResponse> imageResponses = modMediaService.getModImages(gameModId);
        return ResponseEntity.ok(ApiResponse.success("Mod images retrieved successfully", imageResponses));
    }

    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "Delete image", description = "Delete a specific image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Image ID") @PathVariable Long imageId,
            @AuthenticationPrincipal User currentUser) throws Exception {

        modMediaService.deleteImage(gameModId, imageId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully"));
    }

    @PutMapping("/images/{imageId}/name")
    @Operation(summary = "Update image name", description = "Update the name of a specific image")
    public ResponseEntity<ApiResponse<ImageResponse>> updateImageName(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Image ID") @PathVariable Long imageId,
            @Valid @RequestBody ImageUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {

        ImageResponse imageResponse = modMediaService.updateImageName(gameModId, imageId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Image name updated successfully", imageResponse));
    }

    @PostMapping("/youtube-videos")
    @Operation(summary = "Add YouTube videos", description = "Add one or more YouTube video links to the mod")
    public ResponseEntity<ApiResponse<List<YouTubeVideoResponse>>> addYouTubeVideos(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Valid @RequestBody List<YouTubeVideoRequest> requests,
            @AuthenticationPrincipal User currentUser) {

        List<YouTubeVideoResponse> videoResponses = modMediaService.addYouTubeVideos(gameModId, requests, currentUser);
        return ResponseEntity.ok(ApiResponse.success("YouTube videos added successfully", videoResponses));
    }

    @GetMapping("/youtube-videos")
    @Operation(summary = "Get YouTube videos", description = "Retrieve all YouTube videos for a mod")
    public ResponseEntity<ApiResponse<List<YouTubeVideoResponse>>> getYouTubeVideos(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId) {

        List<YouTubeVideoResponse> videoResponses = modMediaService.getYouTubeVideos(gameModId);
        return ResponseEntity.ok(ApiResponse.success("YouTube videos retrieved successfully", videoResponses));
    }

    @PutMapping("/youtube-videos/{videoId}")
    @Operation(summary = "Update YouTube video", description = "Update YouTube video title and description")
    public ResponseEntity<ApiResponse<YouTubeVideoResponse>> updateYouTubeVideo(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Video ID") @PathVariable Long videoId,
            @Valid @RequestBody YouTubeVideoUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {

        YouTubeVideoResponse videoResponse = modMediaService.updateYouTubeVideo(gameModId, videoId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("YouTube video updated successfully", videoResponse));
    }

    @DeleteMapping("/youtube-videos/{videoId}")
    @Operation(summary = "Delete YouTube video", description = "Delete a YouTube video link")
    public ResponseEntity<ApiResponse<Void>> deleteYouTubeVideo(
            @Parameter(description = "Game mod ID") @PathVariable Long gameModId,
            @Parameter(description = "Video ID") @PathVariable Long videoId,
            @AuthenticationPrincipal User currentUser) {

        modMediaService.deleteYouTubeVideo(gameModId, videoId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("YouTube video deleted successfully"));
    }
} 