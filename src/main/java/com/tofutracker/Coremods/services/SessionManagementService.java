package com.tofutracker.Coremods.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionManagementService {

    private final SpringSessionBackedSessionRegistry<?> sessionRegistry;

    @Autowired
    public SessionManagementService(SpringSessionBackedSessionRegistry<?> sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public int terminateAllUserSessions(String username) {
        List<SessionInformation> userSessions = sessionRegistry.getAllSessions(username, false);
        for (SessionInformation session : userSessions) {
            session.expireNow();
        }
        return userSessions.size();
    }
} 