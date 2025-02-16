package com.challenge.emailservice.service;

import com.challenge.emailservice.model.EmailUsage;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.EmailUsageRepository;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.providers.EmailServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.LimitExceededException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public boolean sendEmail(String userEmail, String to, String subject, String body) throws Exception {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new Exception("User not found"));

        EmailUsage emailUsage = emailUsageRepository.findByUser(user)
                .orElseGet(() -> createNewEmailUsage(user));

        // Check if user can send emails
        if (!canSendMoreEmails(emailUsage)) {
            throw new LimitExceededException("User has exceeded the limit of emails per day");
        }

        for (EmailServiceProvider provider : providers) {
            try {
                if (provider.sendEmail(userEmail, to, subject, body)) {
                    updateEmailQuota(emailUsage);
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Error with provider: " + provider.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        return false;
    }

    private boolean canSendMoreEmails(EmailUsage emailUsage) {
        LocalDate today = LocalDate.now();

        if (!today.equals(emailUsage.getDate())) {
            emailUsage.setEmailCount(0);
            emailUsage.setDate(today);
            emailUsageRepository.save(emailUsage);
        }

        return emailUsage.getEmailCount() < 1000;
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
