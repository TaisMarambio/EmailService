package com.challenge.emailservice.controller;

import com.challenge.emailservice.service.email.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> requestMap) {
        return emailService.sendEmail(
                requestMap.get("to"),
                requestMap.get("subject"),
                requestMap.get("body")
        );
    }

}
