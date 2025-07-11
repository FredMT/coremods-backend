package com.tofutracker.Coremods.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "twitch.client")
@Data
public class IgdbConfig {
    private String id;
    private String secret;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 