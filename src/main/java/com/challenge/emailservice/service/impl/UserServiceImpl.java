package com.challenge.emailservice.service.impl;

import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<String> registerUser(Map<String, String> requestMap) {
        log.info("Registering user with username: " + requestMap.get("username") + " and email: " + requestMap.get("email"));
        try {
            if (!validateRegisterMap(requestMap)) {
                return ResponseEntity.badRequest().body("Invalid request");
            }
            User user = userRepository.findByEmail(requestMap.get("email"));
            if (user != null) {
                return ResponseEntity.badRequest().body("User with email already exists");
            }
            userRepository.save(getUserFromMap(requestMap));
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user");
    }

    private boolean validateRegisterMap(Map<String, String> requestMap) {
        return requestMap.containsKey("username") && requestMap.containsKey("email");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setUsername(requestMap.get("username"));
        user.setPassword(requestMap.get("password"));
        user.setEmail(requestMap.get("email"));
        user.setStatus(true);
        user.setRole("USER");
        return user;
    }
}
