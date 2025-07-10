package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.dto.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionManagementService {

    private final SpringSessionBackedSessionRegistry<?> sessionRegistry;

    @Autowired
    public SessionManagementService(SpringSessionBackedSessionRegistry<?> sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public List<SessionInfo> getAllSessions() {
        return sessionRegistry.getAllPrincipals().stream()
            .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream()
                .map(session -> convertToSessionInfo(session, principal.toString())))
            .collect(Collectors.toList());
    }

    public List<SessionInfo> getSessionsByUsername(String username) {
        return sessionRegistry.getAllSessions(username, false).stream()
            .map(session -> convertToSessionInfo(session, username))
            .collect(Collectors.toList());
    }

    public void terminateSession(String sessionId) {
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
        if (sessionInformation != null) {
            sessionInformation.expireNow();
        }
    }
    
    public int terminateAllUserSessions(String username) {
        List<SessionInformation> userSessions = sessionRegistry.getAllSessions(username, false);
        for (SessionInformation session : userSessions) {
            session.expireNow();
        }
        return userSessions.size();
    }
    
    private SessionInfo convertToSessionInfo(SessionInformation session, String principalName) {
        Date lastRequest = session.getLastRequest();
        
        return SessionInfo.builder()
            .sessionId(session.getSessionId())
            .principalName(principalName)
            .creationTime(lastRequest.toInstant())
            .lastAccessTime(lastRequest.toInstant())
            .expiryTime(session.isExpired() ? 
                Instant.now() : 
                Instant.now().plusSeconds(1800))
            .expired(session.isExpired())
            .build();
    }
} 