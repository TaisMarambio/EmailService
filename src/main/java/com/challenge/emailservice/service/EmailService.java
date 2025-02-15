package com.challenge.emailservice.service;

import com.challenge.emailservice.service.providers.EmailServiceProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final List<EmailServiceProvider> providers;

    public EmailService(List<EmailServiceProvider> providers) {
        this.providers = providers;
    }

    public boolean sendEmail(String to, String subject, String body) {
        for (EmailServiceProvider provider : providers) {
            if (provider.sendEmail(to, subject, body)) {
                return true; // Si un proveedor funciona, terminamos el proceso
            }
        }
        return false; // Si todos fallan, se retorna false
    }
}
