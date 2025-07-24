package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.services.ZipTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/mods/{modId}/files/upload")
@RequiredArgsConstructor
public class ZipTestController {

    private final ZipTestService zipTestService;

    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadAndValidateZip(
            @PathVariable Long modId,
            @RequestParam("file") MultipartFile file) {

        try {
            log.info("Validating ZIP file upload for mod ID: {}, filename: {}, size: {} bytes",
                    modId, file.getOriginalFilename(), file.getSize());

            Map<String, Object> validationResult = zipTestService.validateZip(file);

            log.info("ZIP file validation completed successfully for mod ID: {}", modId);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(
                    "ZIP file validation completed successfully",
                    validationResult));

        } catch (IllegalArgumentException | IOException e) {
            log.warn("ZIP file validation failed for mod ID {}: {}", modId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(
                    "ZIP file validation failed: " + e.getMessage()));

        }
    }
}
