package com.tofutracker.Coremods.dto.requests.mods.upload_mod;

import com.tofutracker.Coremods.dto.annotation.ValidExternalRequirement;
import com.tofutracker.Coremods.dto.annotation.ValidModMirror;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModRequirementsMirrorsRequest {

    private List<Long> dlcIds;

    @Valid
    private List<SiteModRequirement> siteMods;

    @Valid
    private List<ExternalRequirement> externalRequirements;

    @Valid
    private List<ModMirror> modMirrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SiteModRequirement {
        private Long modId;
        private String requirementNotes;
    }

    @ValidExternalRequirement
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalRequirement {
        private String name;
        private String url;
        private String notes;
    }

    @ValidModMirror
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModMirror {
        private String mirrorName;
        private String mirrorUrl;
    }
}
