package com.challenge.emailservice.service.providers;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SendGridService.class)
@Tag("ci-ignore")
@TestPropertySource(locations = "classpath:application-test.properties")
class SendGridServiceTest {

    @Autowired
    private SendGridService sendGridService;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Test
    void testSendEmail() {
        String from = "taismmc@gmail.com";
        String to = "taismarambio@hotmail.com";
        String subject = "Prueba de integración con SendGrid";
        String body = "Este es un email de prueba enviado desde SendGridService.";

        boolean result = sendGridService.sendEmail(from, to, subject, body);

        //verifico que SendGrid lo aceptó (Retorna true si el status fue 202)
        assertTrue(result, "El email debería enviarse correctamente a través de SendGrid.");
    }
}
