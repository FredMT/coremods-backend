package com.tofutracker.Coremods.dto.igdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchGameByNameResponse {
    private Long id;

    @JsonProperty("involved_companies")
    private List<InvolvedCompany> involvedCompanies;

    @Data
    public static class InvolvedCompany {
        private Long id;
        private Company company;
    }

    @Data
    public static class Company {
        private Long id;
        private List<PublishedGame> published;
    }

    @Data
    public static class PublishedGame {
        private Long id;
        private String name;
        private String slug;

        @JsonProperty("updated_at")
        private Long updatedAt;

        private Cover cover;
        private List<Platform> platforms;
        private List<DLC> dlcs;
    }

    @Data
    public static class DLC {
        private Long id;
        private String name;
        private String slug;

        @JsonProperty("updated_at")
        private Long updatedAt;

        private Cover cover;
        private List<Platform> platforms;
    }

    @Data
    public static class Cover {
        private Long id;

        @JsonProperty("image_id")
        private String imageId;
    }

    @Data
    public static class Platform {
        private Long id;
        private String name;
    }
}