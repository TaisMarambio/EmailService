package com.challenge.emailservice.service.email;

import com.challenge.emailservice.model.EmailUsage;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.EmailUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailQuotaService {

    private final EmailUsageRepository emailUsageRepository;

    @Autowired
    public EmailQuotaService(EmailUsageRepository emailUsageRepository) {
        this.emailUsageRepository = emailUsageRepository;
    }

    public boolean canSendMoreEmails(User user) {
        EmailUsage emailUsage = emailUsageRepository.findByUserAndDate(user, LocalDate.now())
                .orElseGet(() -> createNewEmailUsage(user));

        //admin can send unlimited emails
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            return true;
        }

        return emailUsage.getEmailCount() < 1000;
    }

    public void updateEmailQuota(User user) {
        EmailUsage emailUsage = emailUsageRepository.findByUserAndDate(user, LocalDate.now())
                .orElseGet(() -> createNewEmailUsage(user));

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
