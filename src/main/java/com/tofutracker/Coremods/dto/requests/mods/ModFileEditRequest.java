package com.tofutracker.Coremods.dto.requests.mods;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.dto.annotation.ValueOfEnum;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModFileEditRequest {

    @Size(max = 50, message = "File name cannot exceed 50 characters")
    private String name;

    @Size(max = 50, message = "File version cannot exceed 50 characters")
    private String version;

    @ValueOfEnum(enumClass = FileCategory.class, message = "File category must be a valid file category")
    private String category;

    @Size(max = 255, message = "File description cannot exceed 255 characters")
    private String description;
} 