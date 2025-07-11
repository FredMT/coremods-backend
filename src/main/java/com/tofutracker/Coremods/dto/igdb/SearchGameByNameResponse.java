package com.tofutracker.Coremods.dto.igdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        /**
         * Returns just the filename part of the cover URL
         * Example: from "//images.igdb.com/igdb/image/upload/t_thumb/co1tnw.jpg"
         * returns "co1tnw.jpg"
         */
        public String getImageId() {
            if (url == null) return null;

            Pattern pattern = Pattern.compile("/([^/]+\\.[a-zA-Z0-9]+)$");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return url;
        }
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
    }

    /**
     * Gets the earliest release date from all available dates
     *
     * @return Unix timestamp as Long or null if no dates available
     */
    public Long getEarliestReleaseDate() {
        if (releaseDates == null || releaseDates.isEmpty()) {
            return null;
        }

        return releaseDates.stream()
                .map(rd -> rd.date)
                .filter(date -> date != null && !date.isEmpty())
                .map(Long::parseLong)
                .min(Long::compare)
                .orElse(null);
    }
} 