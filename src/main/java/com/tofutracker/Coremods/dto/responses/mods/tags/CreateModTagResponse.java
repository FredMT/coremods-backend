package com.tofutracker.Coremods.dto.responses.mods.tags;

import com.tofutracker.Coremods.entity.ModTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateModTagResponse {

    private Long id;
    private Long modId;
    private String tag;

    public static CreateModTagResponse fromEntity(ModTag modTag) {
        if (modTag == null) {
            return null;
        }

        return CreateModTagResponse.builder()
                .id(modTag.getId())
                .modId(modTag.getMod().getId())
                .tag(modTag.getTag())
                .build();
    }
}