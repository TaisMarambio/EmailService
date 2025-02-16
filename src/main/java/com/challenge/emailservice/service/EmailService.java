package com.challenge.emailservice.service;

import com.challenge.emailservice.model.EmailUsage;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.EmailUsageRepository;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.providers.EmailServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.challenge.emailservice.model.Role;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final List<EmailServiceProvider> providers;
    private final UserRepository userRepository;
    private final EmailUsageRepository emailUsageRepository;

    @Autowired
    public EmailService(List<EmailServiceProvider> providers, UserRepository userRepository, EmailUsageRepository emailUsageRepository) {
        this.providers = providers;
        this.userRepository = userRepository;
        this.emailUsageRepository = emailUsageRepository;
    }

    public ResponseEntity<String> sendEmail(String to, String subject, String body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); //obtain authenticated user

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in the database"));

        EmailUsage emailUsage = emailUsageRepository.findByUserAndDate(user, LocalDate.now())
                .orElseGet(() -> createNewEmailUsage(user));

        if (!canSendMoreEmails(emailUsage, user)) { //verify that user can send emails
            return ResponseEntity.status(429).body("User has exceeded the limit of emails per day");
        }

        for (EmailServiceProvider provider : providers) {
            try {
                if (provider.sendEmail(user.getEmail(), to, subject, body)) {
                    updateEmailQuota(emailUsage);
                    return ResponseEntity.ok("Email sent successfully using " + provider.getClass().getSimpleName());
                }
            } catch (Exception e) {
                System.err.println("Error with provider: " + provider.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        return ResponseEntity.status(500).body("Failed to send email with all providers");
    }

    private boolean canSendMoreEmails(EmailUsage emailUsage, User user) {
        LocalDate today = LocalDate.now();

        if (user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()).contains("ROLE_ADMIN")) {
            return true;
        }

        if (!today.equals(emailUsage.getDate())) { //reset email count if it's a new day
            emailUsage.setEmailCount(0);
            emailUsage.setDate(today);
            emailUsageRepository.save(emailUsage);
        }

        return emailUsage.getEmailCount() <= 1000;
    }

    private void updateEmailQuota(EmailUsage emailUsage) {
        emailUsage.incrementEmailCount();
        emailUsageRepository.save(emailUsage);
    }

    private EmailUsage createNewEmailUsage(User user) {
        EmailUsage emailUsage = new EmailUsage();
        emailUsage.setUser(user);
        emailUsage.setEmailCount(0);
        emailUsage.setDate(LocalDate.now());
        return emailUsageRepository.save(emailUsage);
    }
}
