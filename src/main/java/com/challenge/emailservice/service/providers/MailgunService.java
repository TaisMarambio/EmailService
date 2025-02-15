package com.challenge.emailservice.service.providers;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailgunService implements EmailServiceProvider {

    @Value("${mailgun.api.key}")
    private String mailgunApiKey;

    @Value("${mailgun.api.url}")
    private String mailgunApiUrl;

    @Value("${mailgun.sender.email}")
    private String senderEmail;


    @Override
    public boolean sendEmail(String to, String subject, String body) {
        System.out.println("Mailgun API Key: " + mailgunApiKey);
        System.out.println("Mailgun Sender Email: " + senderEmail);
        try {
            HttpResponse<JsonNode> request = Unirest.post(mailgunApiUrl)
                    .basicAuth("api", mailgunApiKey)
                    .queryString("from", senderEmail)
                    .queryString("to", to)
                    .queryString("subject", subject)
                    .queryString("text", body)
                    .asJson();
            System.out.println("Mailgun Response Body: " + request.getBody());
            return request.getStatus() == 200 || request.getStatus() == 202;
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        }
    }
}
