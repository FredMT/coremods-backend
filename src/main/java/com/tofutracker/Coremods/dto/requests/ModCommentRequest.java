package com.tofutracker.Coremods.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModCommentRequest {

    @NotBlank(message = "Comment content is required")
    private String content;

    private Long parentId;
}