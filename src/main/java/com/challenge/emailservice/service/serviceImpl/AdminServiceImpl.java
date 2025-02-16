package com.challenge.emailservice.service.serviceImpl;

import com.challenge.emailservice.model.Role;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.EmailUsageRepository;
import com.challenge.emailservice.repository.RoleRepository;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final EmailUsageRepository emailUsageRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminServiceImpl(EmailUsageRepository emailUsageRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.emailUsageRepository = emailUsageRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Map<String, ? extends Serializable>> getUserStats() {
        return emailUsageRepository.findByDateAndEmailCountGreaterThan(LocalDate.now(), 0)
                .stream()
                .map(emailUsage -> Map.of(
                        "username", emailUsage.getUser().getUsername(),
                        "email", emailUsage.getUser().getEmail(),
                        "email_count", emailUsage.getEmailCount(),
                        "date", emailUsage.getDate()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<String> updateUserRole(String username, String newRole) {
        newRole = newRole.toUpperCase();

        if (!newRole.equals("ROLE_USER") && !newRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.badRequest().body("Invalid role: " + newRole);
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        String finalNewRole = newRole;
        Role roleEntity = roleRepository.findByName(newRole)
                .orElseThrow(() -> new RuntimeException("Role not found: " + finalNewRole));

        // ✅ Hacer mutable la colección de roles
        Set<Role> updatedRoles = new HashSet<>(user.getRoles());
        updatedRoles.clear();
        updatedRoles.add(roleEntity);

        user.setRoles(updatedRoles);
        userRepository.save(user);

        return ResponseEntity.ok("User role updated to " + newRole);
    }

}
