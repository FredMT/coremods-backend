package com.tofutracker.Coremods.config.enums;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IGDBEndpoint {
    public static String GAMES;

    @Value("${igdb.api.base-url}")
    private String baseUrl;

    @PostConstruct
    private void initializeEndpoints() {
        GAMES = baseUrl + "/games";
    }
}