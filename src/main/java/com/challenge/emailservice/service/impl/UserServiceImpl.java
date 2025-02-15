package com.challenge.emailservice.service.impl;

import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<String> registerUser(Map<String, String> requestMap) {
        log.info("Registering user with username: {} and email: {}", requestMap.get("username"), requestMap.get("email"));

        try {
            if (!validateRegisterMap(requestMap)) {
                return ResponseEntity.badRequest().body("Invalid request: missing required fields");
            }

            // Verificar si el usuario ya existe por email o username
            User existingUserByEmail = userRepository.findByEmail(requestMap.get("email"));
            User existingUserByUsername = userRepository.findByUsername(requestMap.get("username"));

            if (existingUserByEmail != null || existingUserByUsername != null) {
                return ResponseEntity.badRequest().body("User with the same email or username already exists");
            }

            // Guardar el nuevo usuario
            userRepository.save(getUserFromMap(requestMap));

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception exception) {
            log.error("Error registering user: {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user");
        }
    }

    private boolean validateRegisterMap(Map<String, String> requestMap) {
        return requestMap.containsKey("username") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password") &&
                !requestMap.get("password").isEmpty();
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setUsername(requestMap.get("username"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password"))); // Encriptar contraseña
        user.setStatus(true);
        user.setRole("USER");
        return user;
    }
}
