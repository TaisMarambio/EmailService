package com.challenge.emailservice.exception;

public class EmailLimitExceededException extends RuntimeException {
    public EmailLimitExceededException(String message) {
        super(message);
    }
}
