package com.challenge.emailservice.service;

import com.challenge.emailservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
    }

    @Test
    void shouldGenerateValidJwtToken() {
        User user = new User();
        user.setUsername("testuser");

        String fakeToken = "fake.jwt.token";
        when(jwtService.generateToken(user)).thenReturn(fakeToken);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals(fakeToken, token);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        User user = new User();
        user.setUsername("testuser");

        String fakeToken = "valid.jwt.token";

        when(jwtService.isTokenValid(fakeToken, user)).thenReturn(true);

        boolean isValid = jwtService.isTokenValid(fakeToken, user);

        assertTrue(isValid);
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String fakeToken = "valid.jwt.token";
        String expectedUsername = "testuser";

        when(jwtService.extractUserName(fakeToken)).thenReturn(expectedUsername);

        String username = jwtService.extractUserName(fakeToken);

        assertEquals(expectedUsername, username);
    }
}
