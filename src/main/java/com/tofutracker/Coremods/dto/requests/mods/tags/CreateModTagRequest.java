package com.tofutracker.Coremods.dto.requests.mods.tags;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateModTagRequest {

    @NotBlank(message = "Tag is required")
    @Size(min = 2, max = 50, message = "Tag must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Tag can only contain letters, numbers, hyphens, and underscores")
    private String tag;
}