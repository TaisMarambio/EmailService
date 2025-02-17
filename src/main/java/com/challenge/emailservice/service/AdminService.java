package com.challenge.emailservice.service;

import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface AdminService {
    List<Map<String, ? extends Serializable>> getUserStats();
    ResponseEntity<String> updateUserRole(String username, String newRole);
}
