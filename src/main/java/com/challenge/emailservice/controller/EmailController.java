package com.challenge.emailservice.controller;

import com.challenge.emailservice.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String to,
                                            @RequestParam String subject,
                                            @RequestParam String body) {
        boolean success = emailService.sendEmail(to, subject, body);
        if (success) {
            return ResponseEntity.ok("Email sent successfully!");
        } else {
            return ResponseEntity.status(500).body("Failed to send email.");
        }
    }
}
