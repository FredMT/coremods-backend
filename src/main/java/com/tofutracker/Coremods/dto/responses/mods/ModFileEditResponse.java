package com.tofutracker.Coremods.dto.responses.mods;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.entity.ModFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModFileEditResponse {
    
    private Long id;
    private String name;
    private String version;
    private FileCategory category;
    private String description;
    private LocalDateTime updatedAt;
    
    public static ModFileEditResponse fromEntity(ModFile modFile) {
        return ModFileEditResponse.builder()
                .id(modFile.getId())
                .name(modFile.getName())
                .version(modFile.getVersion())
                .category(modFile.getCategory())
                .description(modFile.getDescription())
                .updatedAt(modFile.getUpdatedAt())
                .build();
    }
} 