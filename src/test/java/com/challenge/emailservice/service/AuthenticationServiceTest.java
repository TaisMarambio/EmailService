package com.challenge.emailservice.service;

import com.challenge.emailservice.dao.request.LoginRequest;
import com.challenge.emailservice.dao.request.RegisterRequest;
import com.challenge.emailservice.dao.response.JwtAuthenticationResponse;
import com.challenge.emailservice.model.Role;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.RoleRepository;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.serviceImpl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuthenticationService authService = new AuthenticationServiceImpl(userRepository, passwordEncoder, jwtService, roleRepository);

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Set.of(new Role(1, "ROLE_USER", null)));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("fake-jwt-token");
    }

    @Test
    void shouldLoginSuccessfullyWithCorrectCredentials() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        JwtAuthenticationResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
    }

    @Test
    void shouldFailLoginWithIncorrectPassword() {
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");

        Exception exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("Invalid username or password.", exception.getMessage());
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest("newuser", "newuser@example.com", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role(1, "ROLE_USER", null)));
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        JwtAuthenticationResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
    }

    @Test
    void shouldFailRegistrationIfUsernameAlreadyExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        RegisterRequest registerRequest = new RegisterRequest("testuser", "newemail@example.com", "password123");

        Exception exception = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("Username already exists.", exception.getMessage());
    }
}
