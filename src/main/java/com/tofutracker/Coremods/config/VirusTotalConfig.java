package com.tofutracker.Coremods.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class VirusTotalConfig {

    @Value("${virustotal.api.key}")
    private String apiKey;
    
    @Value("${virustotal.api.base-url:https://www.virustotal.com/api/v3}")
    private String baseUrl;
    
    @Bean
    public RestClient virusTotalRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-apikey", apiKey)
                .build();
    }
} 