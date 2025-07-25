package com.tofutracker.Coremods.dto.responses.mods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModFileUploadResponse {
    
    private String progressId;
    private Map<String, Object> validationResult;
    private String status;
    
} 