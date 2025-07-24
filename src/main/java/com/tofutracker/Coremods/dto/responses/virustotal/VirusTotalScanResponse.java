package com.tofutracker.Coremods.dto.responses.virustotal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirusTotalScanResponse {
    private VirusTotalData data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VirusTotalData {
        private String type;
        private String id;
    }
} 