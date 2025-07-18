package com.tofutracker.Coremods.dto.requests.mods.comments;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModCommentUpdateRequest {

    @NotBlank(message = "Comment content is required")
    private String content;
}