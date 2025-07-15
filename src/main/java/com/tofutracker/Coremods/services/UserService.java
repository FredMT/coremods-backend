package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.config.enums.Role;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.repository.UserRepository;
import com.tofutracker.Coremods.exception.RoleAlreadyAssignedException;
import com.tofutracker.Coremods.services.email.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final SessionManagementService sessionManagementService;

    public void registerUser(String username, String email, String password) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .emailVerified(false)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        User savedUser = userRepository.save(user);

        emailVerificationService.generateVerificationToken(savedUser);

    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void markEmailAsVerified(Long userId) {
        userRepository.markEmailAsVerified(userId);
    }

    @Transactional
    public boolean validateCurrentPassword(User user, String currentPassword) {
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {
        User freshUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + user.getId()));
        
        freshUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(freshUser);
        log.info("Password updated successfully for user: {}", freshUser.getUsername());
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // Only update if the role is actually changing
        if (user.getRole() != newRole) {
            log.info("Updating role for user {} from {} to {}", user.getUsername(), user.getRole(), newRole);
            
            user.setRole(newRole);
            userRepository.save(user);
            
            // Terminate all sessions for this user to force re-login with new permissions
            int terminatedCount = sessionManagementService.terminateAllUserSessions(user.getUsername());
            log.info("Terminated {} sessions for user {} after role update", terminatedCount, user.getUsername());
        } else {
            log.info("Role for user {} is already {}, no update needed", user.getUsername(), newRole);
            throw new RoleAlreadyAssignedException("User already has role: " + newRole);
        }
    }
} 