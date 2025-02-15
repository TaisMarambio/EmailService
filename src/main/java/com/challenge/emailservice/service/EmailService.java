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
            try {
                if (provider.sendEmail(to, subject, body)) {
                    return true; //si un proveedor funciona termina el proceso
                }
            } catch (Exception e) {
                System.err.println("Error con proveedor: " + provider.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        return false; //si todos fallan, devuelve false
    }
}
