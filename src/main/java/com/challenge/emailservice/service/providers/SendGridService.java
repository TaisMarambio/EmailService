package com.challenge.emailservice.service.providers;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
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

            // Log para depurar la respuesta de SendGrid
            System.out.println("SendGrid Response Code: " + response.getStatusCode());
            System.out.println("SendGrid Response Body: " + response.getBody());
            System.out.println("SendGrid Response Headers: " + response.getHeaders());

            return response.getStatusCode() == 202;
        } catch (IOException ex) {
            // Log para depurar errores de conexión
            System.err.println("Error al enviar email con SendGrid: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}
