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
public class ModFileResponse {
    
    private Long id;
    private Long modId;
    private String name;
    private String originalFilename;
    private Long size;
    private String ext;
    private String description;
    private String version;
    private FileCategory category;
    private Long downloadCount;
    private String fileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ModFileResponse fromEntity(ModFile modFile, String fileUrl) {
        return ModFileResponse.builder()
                .id(modFile.getId())
                .modId(modFile.getMod().getId())
                .name(modFile.getName())
                .originalFilename(modFile.getOriginalFilename())
                .size(modFile.getSize())
                .ext(modFile.getExt())
                .description(modFile.getDescription())
                .version(modFile.getVersion())
                .category(modFile.getCategory())
                .downloadCount(modFile.getDownloadCount())
                .fileUrl(fileUrl)
                .createdAt(modFile.getCreatedAt())
                .updatedAt(modFile.getUpdatedAt())
                .build();
    }
} 