package com.challenge.emailservice.controller;

import com.challenge.emailservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping(path = "/register")
    public ResponseEntity<String> registerUser(@RequestBody(required = true) Map<String, String> requestMap) {
        try {
            return userService.registerUser(requestMap);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return ResponseEntity.internalServerError().body("Error registering user");
    }
}
