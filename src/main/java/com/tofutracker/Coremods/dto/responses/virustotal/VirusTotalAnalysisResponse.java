package com.tofutracker.Coremods.dto.responses.virustotal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirusTotalAnalysisResponse {
    private Data data;

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String id;
        private String type;
        private Attributes attributes;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {
        private Long date;
        private Map<String, AnalysisResult> results;
        private AnalysisStats stats;
        private String status;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalysisResult {
        private String category;
        
        @JsonProperty("engine_name")
        private String engineName;
        
        @JsonProperty("engine_update")
        private String engineUpdate;
        
        @JsonProperty("engine_version")
        private String engineVersion;
        
        private String method;
        private String result;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalysisStats {
        @JsonProperty("confirmed-timeout")
        private Integer confirmedTimeout;
        
        private Integer failure;
        private Integer harmless;
        private Integer malicious;
        private Integer suspicious;
        private Integer timeout;
        
        @JsonProperty("type-unsupported")
        private Integer typeUnsupported;
        
        private Integer undetected;
    }
} 