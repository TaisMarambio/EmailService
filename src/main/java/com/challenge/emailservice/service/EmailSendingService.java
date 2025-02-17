package com.challenge.emailservice.service;

import org.springframework.http.ResponseEntity;

public interface EmailSendingService {
    ResponseEntity<String> sendEmail(String to, String subject, String body);
}
