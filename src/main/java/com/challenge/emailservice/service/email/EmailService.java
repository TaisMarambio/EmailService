package com.challenge.emailservice.service.email;

import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.EmailSendingService;
import com.challenge.emailservice.service.providers.EmailServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService implements EmailSendingService {

    private final List<EmailServiceProvider> providers;
    private final UserRepository userRepository;
    private final EmailQuotaService emailQuotaService;

    @Autowired
    public EmailService(List<EmailServiceProvider> providers, UserRepository userRepository, EmailQuotaService emailQuotaService) {
        this.providers = providers;
        this.userRepository = userRepository;
        this.emailQuotaService = emailQuotaService;
    }

    @Override
    public ResponseEntity<String> sendEmail(String to, String subject, String body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // Obtain authenticated user

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in the database"));

        if (!emailQuotaService.canSendMoreEmails(user)) { // Verify that the user can send emails
            return ResponseEntity.status(429).body("User has exceeded the limit of emails per day");
        }

        for (EmailServiceProvider provider : providers) {
            try {
                if (provider.sendEmail(user.getEmail(), to, subject, body)) { // Use the authenticated user's email as sender
                    emailQuotaService.updateEmailQuota(user);
                    return ResponseEntity.ok("Email sent successfully using " + provider.getClass().getSimpleName());
                }
            } catch (Exception e) {
                System.err.println("Error with provider: " + provider.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        return ResponseEntity.status(500).body("Failed to send email with all providers");
    }
}
