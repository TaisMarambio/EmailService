package com.challenge.emailservice.service;

import com.challenge.emailservice.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService {
    ResponseEntity<String> registerUser(Map<String,String> requestMap);
}
