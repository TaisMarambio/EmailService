package com.challenge.emailservice.service.providers;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@Slf4j
public class SendGridService implements EmailServiceProvider {

    @Value("${sendgrid.api.key:default_key}")
    private String sendGridApiKey;

    @Override
    public boolean sendEmail(String senderEmail, String to, String subject, String body) {

        Email from = new Email(senderEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            //logs
            log.info("📧 SendGrid Response Code: {}", response.getStatusCode());
            log.info("📧 SendGrid Response Body: {}", response.getBody());
            log.info("📧 SendGrid Response Headers: {}", response.getHeaders());

            return response.getStatusCode() == 202;
        } catch (IOException ex) {
            //connection error logs
            log.error("Error when sending email with SendGrid: {}", ex.getMessage(), ex);
            return false;
        }
    }
}
