package com.challenge.emailservice.service.providers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailgunService implements EmailServiceProvider {

    @Value("${mailgun.api.key:default_key}")
    private String mailgunApiKey;

    @Value("${mailgun.api.url:default_url}")
    private String mailgunApiUrl;

    @Override
    public boolean sendEmail(String senderEmail, String to, String subject, String body) {
        try {
            HttpResponse<JsonNode> request = Unirest.post(mailgunApiUrl)
                    .basicAuth("api", mailgunApiKey)
                    .queryString("from", senderEmail)
                    .queryString("to", to)
                    .queryString("subject", subject)
                    .queryString("text", body)
                    .asJson();

            log.info("Mailgun Response: {} - {}", request.getStatus(), request.getBody());
            return request.getStatus() == 200 || request.getStatus() == 202;
        } catch (UnirestException e) {
            System.err.println("Mailgun Error: " + e.getMessage());
            return false;
        }
    }
}
