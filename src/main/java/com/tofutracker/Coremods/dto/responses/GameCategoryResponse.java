package com.tofutracker.Coremods.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameCategoryResponse {
    private Long id;
    private String name;
    private boolean approved;
    private Long parentId;
    private String parentName;
    private boolean isRootCategory;
    private boolean hasChildren;
    
    @Builder.Default
    private List<GameCategoryResponse> children = new ArrayList<>();
    
    public void addChild(GameCategoryResponse child) {
        this.children.add(child);
        this.hasChildren = true;
    }
    
    public boolean isSubcategory() {
        return this.parentId != null;
    }
} 