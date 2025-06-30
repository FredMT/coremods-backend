package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.repository.UserRepository;
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
        log.info("Password updated successfully for user: {}", user.getUsername());
    }

} 