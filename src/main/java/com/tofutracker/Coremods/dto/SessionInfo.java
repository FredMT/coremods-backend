package com.tofutracker.Coremods.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfo {
    private String sessionId;
    private String principalName;
    private Instant creationTime;
    private Instant lastAccessTime;
    private Instant expiryTime;
    private boolean expired;
} 