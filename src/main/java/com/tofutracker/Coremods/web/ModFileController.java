package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.dto.requests.mods.upload_files.ModFileUploadRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.ModFileResponse;
import com.tofutracker.Coremods.dto.responses.mods.ModFileUploadResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mods/{modId}/files")
@RequiredArgsConstructor
public class ModFileController {

    private final ModFileService modFileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ModFileUploadResponse>> uploadModFile(
            @PathVariable Long modId,
            @RequestParam("archiveFile") MultipartFile archiveFile,
            @RequestParam("fileName") String fileName,
            @RequestParam("fileVersion") String fileVersion,
            @RequestParam("fileCategory") FileCategory fileCategory,
            @RequestParam(value = "fileDescription", required = false) String fileDescription,
            @RequestParam("isNewVersionOfExistingFile") Boolean isNewVersionOfExistingFile,
            @RequestParam(value = "fileId", required = false) Long fileId,
            @RequestParam(value = "removePreviousFileVersion", required = false) Boolean removePreviousFileVersion,
            @AuthenticationPrincipal User currentUser) throws IOException {
        
        ModFileUploadRequest request = ModFileUploadRequest.builder()
                .archiveFile(archiveFile)
                .fileName(fileName)
                .fileVersion(fileVersion)
                .fileCategory(fileCategory)
                .fileDescription(fileDescription)
                .isNewVersionOfExistingFile(isNewVersionOfExistingFile)
                .fileId(fileId)
                .removePreviousFileVersion(removePreviousFileVersion)
                .build();

        ModFileUploadResponse response = modFileService.startModFileUpload(modId, request, currentUser);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("File upload started successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModFileResponse>> getModFile(
            @PathVariable Long modId,
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        ModFileResponse response = modFileService.getModFile(id);
        return ResponseEntity.ok(ApiResponse.success("Mod file retrieved successfully", response));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ModFileResponse>>> getModFilesByMod(
            @PathVariable Long modId,
            @AuthenticationPrincipal User currentUser) {
        List<ModFileResponse> response = modFileService.getModFilesByMod(modId);
        return ResponseEntity.ok(ApiResponse.success("Mod files retrieved successfully", response));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ModFileResponse>>> getModFilesByModAndCategory(
            @PathVariable Long modId, 
            @PathVariable FileCategory category,
            @AuthenticationPrincipal User currentUser) {
        List<ModFileResponse> response = modFileService.getModFilesByModAndCategory(modId, category);
        return ResponseEntity.ok(ApiResponse.success("Mod files retrieved successfully", response));
    }

    @PostMapping("/{fileId}/archive")
    public ResponseEntity<ApiResponse<Void>> archiveModFile(
            @PathVariable Long modId,
            @PathVariable Long fileId,
            @AuthenticationPrincipal User currentUser) {
        modFileService.archiveModFile(modId, fileId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Mod file archived successfully"));
    }

    @PostMapping("/{fileId}/download")
    public ResponseEntity<ApiResponse<ModFileResponse>> incrementDownloadCount(
            @PathVariable Long modId,
            @PathVariable Long fileId) {
        ModFileResponse response = modFileService.incrementDownloadCount(fileId);
        return ResponseEntity.ok(ApiResponse.success("Download count incremented successfully", response));
    }
} 