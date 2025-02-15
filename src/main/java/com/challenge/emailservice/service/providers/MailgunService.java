package com.challenge.emailservice.service.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class MailgunService implements EmailServiceProvider {

    @Value("${mailgun.api.key}")
    private String mailgunApiKey; // Se obtiene desde application.properties

    @Value("${mailgun.api.url}")
    private String mailgunApiUrl; // URL de Mailgun desde properties

    @Value("${mailgun.sender.email}")
    private String senderEmail;


    @Override
    public boolean sendEmail(String to, String subject, String body) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("api", mailgunApiKey); // Usa la API Key obtenida
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("from", "noreply@tu-dominio.com");
        requestBody.add("to", to);
        requestBody.add("subject", subject);
        requestBody.add("text", body);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(mailgunApiUrl, request, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }
}
