package com.tofutracker.Coremods.dto.igdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchGameByNameResponse {
    private Long id;
    private String name;
    private String summary;
    
    @JsonProperty("cover")
    private Cover cover;
    
    @JsonProperty("release_dates")
    private List<ReleaseDate> releaseDates;
    
    @JsonProperty("platforms")
    private List<Platform> platforms;
    
    @Data
    public static class Cover {
        private Long id;
        private String url;
    }
    
    @Data
    public static class ReleaseDate {
        private Long id;
        private String date; // Unix timestamp as string

    }
    
    @Data
    public static class Platform {
        private Long id;
        private String name;
        private String abbreviation;
    }
} 