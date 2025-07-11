package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.config.IgdbConfig;
import com.tofutracker.Coremods.dto.TwitchTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class IgdbAuthService {
    
    private final RestTemplate restTemplate;
    private final IgdbConfig igdbConfig;
    
    private final AtomicReference<String> accessToken = new AtomicReference<>();
    private final AtomicReference<Instant> tokenExpiryTime = new AtomicReference<>();
    
    /**
     * Creates HTTP headers for IGDB API requests with authentication
     * @return HttpHeaders configured for IGDB API requests
     */
    public HttpHeaders createIgdbHeaders() {
        String token = getAccessToken();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Client-ID", igdbConfig.getId());
        headers.set("Authorization", "Bearer " + token);
        
        return headers;
    }
    
    /**
     * Creates an HttpEntity with IGDB headers and the provided request body
     * @param requestBody The request body to include in the entity
     * @return HttpEntity configured for IGDB API requests
     */
    public HttpEntity<String> createIgdbHttpEntity(String requestBody) {
        return new HttpEntity<>(requestBody, createIgdbHeaders());
    }
    
    public String getAccessToken() {
        if (isTokenValid()) {
            return accessToken.get();
        }
        return refreshToken();
    }
    
    private boolean isTokenValid() {
        String token = accessToken.get();
        Instant expiry = tokenExpiryTime.get();
        
        if (token == null || expiry == null) {
            return false;
        }
        
        // Check if token expires within next 5 minutes
        return Instant.now().isBefore(expiry.minusSeconds(300));
    }
    
    private String refreshToken() {
        log.info("Refreshing IGDB access token");
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://id.twitch.tv/oauth2/token")
                .queryParam("client_id", igdbConfig.getId())
                .queryParam("client_secret", igdbConfig.getSecret())
                .queryParam("grant_type", "client_credentials");
                
        try {
            ResponseEntity<TwitchTokenResponse> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    HttpEntity.EMPTY,
                    TwitchTokenResponse.class);
            
            TwitchTokenResponse tokenResponse = response.getBody();
            if (tokenResponse != null) {
                accessToken.set(tokenResponse.getAccessToken());
                tokenExpiryTime.set(Instant.now().plusSeconds(tokenResponse.getExpiresIn()));
                
                log.info("Successfully refreshed IGDB access token, expires at: {}", 
                        tokenExpiryTime.get());
                
                return tokenResponse.getAccessToken();
            } else {
                log.error("Failed to refresh IGDB access token: Empty response body");
                throw new RuntimeException("Failed to refresh IGDB access token");
            }
        } catch (Exception e) {
            log.error("Failed to refresh IGDB access token", e);
            throw new RuntimeException("Failed to refresh IGDB access token", e);
        }
    }
} 