package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.responses.virustotal.VirusTotalAnalysisResponse;
import com.tofutracker.Coremods.dto.responses.virustotal.VirusTotalScanResponse;
import com.tofutracker.Coremods.dto.responses.virustotal.VirusTotalUploadUrlResponse;
import com.tofutracker.Coremods.services.virustotal.VirusTotalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/virustotal")
@RequiredArgsConstructor
@Slf4j
public class VirusTotalController {

    private final VirusTotalService virusTotalService;

    @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VirusTotalScanResponse scanFile(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        String fileName = file.getOriginalFilename();
        
        if (fileBytes.length <= 32 * 1024 * 1024) {
            return virusTotalService.uploadFile(fileBytes, fileName);
        } else {
            VirusTotalUploadUrlResponse uploadUrlResponse = virusTotalService.getUploadUrl();
            return virusTotalService.uploadLargeFile(uploadUrlResponse.getData(), fileBytes, fileName);
        }
    }

    @GetMapping("/upload-url")
    public VirusTotalUploadUrlResponse getUploadUrl() {
        return virusTotalService.getUploadUrl();
    }

    @GetMapping("/analysis/{analysisId}")
    public VirusTotalAnalysisResponse getAnalysisResults(@PathVariable String analysisId) {
        return virusTotalService.getAnalysisResults(analysisId);
    }
} 