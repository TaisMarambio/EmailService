package com.challenge.emailservice.loader;

import com.challenge.emailservice.model.Role;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.RoleRepository;
import com.challenge.emailservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() {
        initRoles();
        initAdminUser();
    }

    private void initRoles() {
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        for (String roleName : roles) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName(roleName);
                        return roleRepository.save(role);
                    });
        }

        System.out.println("Roles initialized!");
    }

    private void initAdminUser() {
        String adminUsername = "admin";
        String adminEmail = "admin@example.com";

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found!"));

            User adminUser = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123")) //encrypted password
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(adminUser);
            System.out.println("Admin user created: " + adminUsername);
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
