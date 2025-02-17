package com.challenge.emailservice.service;

import com.challenge.emailservice.dao.request.LoginRequest;
import com.challenge.emailservice.dao.request.RegisterRequest;
import com.challenge.emailservice.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse register(RegisterRequest request);

    JwtAuthenticationResponse login(LoginRequest request);
}