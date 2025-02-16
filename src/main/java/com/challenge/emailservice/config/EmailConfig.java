package com.challenge.emailservice.config;

import com.challenge.emailservice.service.providers.EmailServiceProvider;
import com.challenge.emailservice.service.providers.SendGridService;
import com.challenge.emailservice.service.providers.MailgunService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EmailConfig {
    @Bean
    public List<EmailServiceProvider> emailProviders(SendGridService sendGridService, MailgunService mailgunService) {
        return List.of(sendGridService, mailgunService);
    }
}
