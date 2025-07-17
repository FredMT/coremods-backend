package com.tofutracker.Coremods.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModCommentResponse {

    private Long id;
    private String content;
    private String username;
    private Long parentId;
    private boolean isDeleted;
    private boolean isUpdated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}