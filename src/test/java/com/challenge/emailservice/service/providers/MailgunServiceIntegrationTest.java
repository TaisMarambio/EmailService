package com.challenge.emailservice.service.providers;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MailgunService.class)
@TestPropertySource(locations = "classpath:application.properties")
class MailgunServiceIntegrationTest {

    @Autowired
    private MailgunService mailgunService;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Test
    void testSendEmail() {
        String to = "taismmc@gmail.com";
        String subject = "Prueba de integración con Mailgun";
        String body = "Este es un email de prueba enviado desde MailgunService.";

        boolean result = mailgunService.sendEmail(to, subject, body);

        //verifico que mailgun lo aceptó (Retorna true si el status fue 202)
        assertTrue(result, "El email debería enviarse correctamente a través de Mailgun.");
    }
}
