package com.challenge.emailservice.controller;

import com.challenge.emailservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats() {
        return ResponseEntity.ok(adminService.getUserStats());
    }

    @PatchMapping("/updateRole")
    public ResponseEntity<String> updateUserRole(@RequestBody Map<String, String> requestMap) {
        String username = requestMap.get("username");
        String newRole = requestMap.get("role");
        return adminService.updateUserRole(username, newRole);
    }
}
