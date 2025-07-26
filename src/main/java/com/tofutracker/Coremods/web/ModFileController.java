package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.dto.requests.mods.ModFileEditRequest;
import com.tofutracker.Coremods.dto.requests.mods.upload_files.ModFileUploadRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.ModFileEditResponse;
import com.tofutracker.Coremods.dto.responses.mods.ModFileUploadResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
        
        return ResponseEntity.ok(ApiResponse.success("File upload started successfully", response));
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<ApiResponse<ModFileEditResponse>> editModFile(
            @PathVariable Long modId,
            @PathVariable Long fileId,
            @Valid @RequestBody ModFileEditRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        ModFileEditResponse response = modFileService.editModFile(modId, fileId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Mod file details updated successfully", response));
    }
} 