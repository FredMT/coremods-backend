package com.tofutracker.Coremods.services.virustotal;

import com.tofutracker.Coremods.dto.responses.virustotal.VirusTotalAnalysisResponse;
import com.tofutracker.Coremods.dto.responses.virustotal.VirusTotalScanResponse;
import com.tofutracker.Coremods.dto.responses.virustotal.VirusTotalUploadUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class VirusTotalService {

    private final RestClient virusTotalRestClient;
    
    @Value("${virustotal.api.key}")
    private String apiKey;

    /**
     * Upload a file to VirusTotal for scanning (for files under 32MB)
     *
     * @param fileBytes the file content as byte array
     * @param fileName the name of the file
     * @return the scan response containing the analysis ID
     */
    public VirusTotalScanResponse uploadFile(byte[] fileBytes, String fileName) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        body.add("file", fileResource);

        return virusTotalRestClient.post()
                .uri("/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(VirusTotalScanResponse.class);
    }

    /**
     * Get a special upload URL for large files (over 32MB)
     *
     * @return the upload URL response
     */
    public VirusTotalUploadUrlResponse getUploadUrl() {
        return virusTotalRestClient.get()
                .uri("/files/upload_url")
                .retrieve()
                .body(VirusTotalUploadUrlResponse.class);
    }

    /**
     * Upload a large file to VirusTotal using the special upload URL
     *
     * @param uploadUrl the special upload URL
     * @param fileBytes the file content as byte array
     * @param fileName the name of the file
     * @return the scan response containing the analysis ID
     */
    public VirusTotalScanResponse uploadLargeFile(String uploadUrl, byte[] fileBytes, String fileName) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        body.add("file", fileResource);

        RestClient uploadClient = RestClient.builder()
                .defaultHeader("x-apikey", apiKey)
                .build();
        
        return uploadClient.post()
                .uri(uploadUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(VirusTotalScanResponse.class);
    }

    /**
     * Get the analysis results for a file using the analysis ID
     *
     * @param analysisId the ID of the analysis to get results for
     * @return the analysis response containing scan results
     */
    public VirusTotalAnalysisResponse getAnalysisResults(String analysisId) {
        return virusTotalRestClient.get()
                .uri("/analyses/{id}", analysisId)
                .retrieve()
                .body(VirusTotalAnalysisResponse.class);
    }
} 