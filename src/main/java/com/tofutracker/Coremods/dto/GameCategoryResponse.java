package com.tofutracker.Coremods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameCategoryResponse {
    private Long id;
    private String name;
    private boolean approved;
} 