package com.tofutracker.Coremods.dto.responses.mods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DLCResponse {
    private Long id;
    private String name;
}