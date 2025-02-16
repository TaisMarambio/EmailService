package com.challenge.emailservice.service;

import com.challenge.emailservice.service.providers.EmailServiceProvider;
import com.challenge.emailservice.service.providers.SendGridService;
import com.challenge.emailservice.service.providers.MailgunService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;  // Inject the real EmailService

    @Autowired
    private List<EmailServiceProvider> providers;  // Inject the list of providers

    @Test
    void providersShouldBeInjected() {
        assertNotNull(providers, "The provider list should not be null");
        assertFalse(providers.isEmpty(), "The provider list should not be empty");

        //print providers para verificar
        System.out.println("Loaded Providers:");
        providers.forEach(provider -> System.out.println(provider.getClass().getSimpleName()));

        // Ensure specific implementations are present
        boolean hasSendGrid = providers.stream().anyMatch(p -> p instanceof SendGridService);
        boolean hasMailgun = providers.stream().anyMatch(p -> p instanceof MailgunService);

        assertTrue(hasSendGrid, "SendGridService should be present in the provider list");
        assertTrue(hasMailgun, "MailgunService should be present in the provider list");
    }
}
