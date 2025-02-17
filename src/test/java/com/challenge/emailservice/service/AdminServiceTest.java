package com.challenge.emailservice.service;

import com.challenge.emailservice.model.Role;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.RoleRepository;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.serviceImpl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final AdminService adminService = new AdminServiceImpl(null, userRepository, roleRepository);

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setRoles(new HashSet<>(List.of(new Role(1, "ROLE_USER", new HashSet<>()))));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role(2, "ROLE_ADMIN", new HashSet<>())));
    }

    @Test
    void adminShouldUpdateUserRoleSuccessfully() {
        ResponseEntity<String> response = adminService.updateUserRole("testuser", "ROLE_ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User role updated to ROLE_ADMIN", response.getBody());
    }

    @Test
    void adminShouldFailToUpdateUserRoleForInvalidRole() {
        ResponseEntity<String> response = adminService.updateUserRole("testuser", "ROLE_INVALID");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid role: ROLE_INVALID", response.getBody());
    }

    @Test
    void adminShouldFailToUpdateRoleForNonExistentUser() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        ResponseEntity<String> response = adminService.updateUserRole("unknownUser", "ROLE_ADMIN");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }
}
