package com.challenge.emailservice.service;

import com.challenge.emailservice.model.EmailUsage;
import com.challenge.emailservice.model.Role;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.EmailUsageRepository;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.email.EmailQuotaService;
import com.challenge.emailservice.service.email.EmailService;
import com.challenge.emailservice.service.providers.EmailServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class EmailServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final EmailUsageRepository emailUsageRepository = mock(EmailUsageRepository.class);
    private final EmailServiceProvider provider = mock(EmailServiceProvider.class);
    private final EmailQuotaService emailQuotaService = new EmailQuotaService(emailUsageRepository);
    private final List<EmailServiceProvider> providers = List.of(provider);

    private final EmailService emailService = new EmailService(providers, userRepository, emailQuotaService);

    @BeforeEach
    void setUp() {
        //SecurityContext and authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");

        //mocked security context
        SecurityContextHolder.setContext(securityContext);

        //authenticated user exists in the mock database?
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setRoles(Set.of(new Role(1, "ROLE_USER", new HashSet<>())));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void userShouldSendEmailIfWithinLimit() {
        EmailUsage emailUsage = new EmailUsage();
        emailUsage.setEmailCount(999);

        when(emailUsageRepository.findByUserAndDate(any(User.class), any(LocalDate.class)))
                .thenReturn(Optional.of(emailUsage));
        when(provider.sendEmail(any(), any(), any(), any())).thenReturn(true);

        ResponseEntity<String> response = emailService.sendEmail("recipient@example.com", "Test email", "This is a test!");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void userShouldNotSendEmailIfLimitExceeded() {
        //Mock email usage (exceeds limit)
        EmailUsage emailUsage = new EmailUsage();
        emailUsage.setEmailCount(1001);
        emailUsage.setDate(LocalDate.now());

        when(emailUsageRepository.findByUserAndDate(any(User.class), any(LocalDate.class)))
                .thenReturn(Optional.of(emailUsage));

        ResponseEntity<String> response = emailService.sendEmail("recipient@example.com", "Test email", "This is a test!");

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
    }
}