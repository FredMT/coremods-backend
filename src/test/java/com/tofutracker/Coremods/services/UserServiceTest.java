package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.config.enums.Role;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.RoleAlreadyAssignedException;
import com.tofutracker.Coremods.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionManagementService sessionManagementService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void updateUserRole_ShouldUpdateRoleAndTerminateSessions() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionManagementService.terminateAllUserSessions("testuser")).thenReturn(2);

        // Act
        userService.updateUserRole(1L, Role.MODERATOR);

        // Assert
        verify(userRepository).save(testUser);
        verify(sessionManagementService).terminateAllUserSessions("testuser");
        assertEquals(Role.MODERATOR, testUser.getRole());
    }

    @Test
    void updateUserRole_ShouldThrowExceptionWhenSameRole() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        RoleAlreadyAssignedException exception = assertThrows(
            RoleAlreadyAssignedException.class,
            () -> userService.updateUserRole(1L, Role.USER)
        );
        
        assertEquals("User already has role: USER", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(sessionManagementService, never()).terminateAllUserSessions(anyString());
    }
} 