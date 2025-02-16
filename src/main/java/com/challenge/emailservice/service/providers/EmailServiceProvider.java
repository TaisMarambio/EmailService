package com.challenge.emailservice.service.providers;

public interface EmailServiceProvider{
    boolean sendEmail(String from, String to, String subject, String body);
}
