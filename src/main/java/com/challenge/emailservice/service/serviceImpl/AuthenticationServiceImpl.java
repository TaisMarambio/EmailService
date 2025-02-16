package com.challenge.emailservice.service.serviceImpl;

import com.challenge.emailservice.dao.request.LoginRequest;
import com.challenge.emailservice.dao.request.RegisterRequest;
import com.challenge.emailservice.dao.response.JwtAuthenticationResponse;
import com.challenge.emailservice.model.Role;
import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.RoleRepository;
import com.challenge.emailservice.repository.UserRepository;
import com.challenge.emailservice.service.AuthenticationService;
import com.challenge.emailservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    @Override
    public JwtAuthenticationResponse register(RegisterRequest request) {
        // 🔹 Validamos datos de entrada
        validateRegisterRequest(request);

        // 🔹 Buscar `ROLE_USER` en la base de datos o crearlo si no existe
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    log.warn("⚠️ ROLE_USER not found. Creating it automatically.");
                    return roleRepository.save(new Role(null, "ROLE_USER", null));
                });

        // 🔹 Crear y guardar el usuario
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // ✅ Asegurar codificación
                .roles(Set.of(userRole)) // ✅ Asignamos `ROLE_USER`
                .build();

        userRepository.save(user);
        log.info("✅ New user registered: {}", user.getUsername());

        // 🔹 Generar JWT y devolver la respuesta
        return createJwtResponse(user);
    }

    @Override
    public JwtAuthenticationResponse login(LoginRequest request) {
        // 🔹 Buscar usuario en la base de datos
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

        // 🔹 Validar la contraseña encriptada
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("❌ Failed login attempt for user: {}", user.getUsername());
            throw new BadCredentialsException("Invalid username or password.");
        }

        log.info("✅ User logged in: {}", user.getUsername());

        // 🔹 Generar JWT y devolver la respuesta
        return createJwtResponse(user);
    }

    /**
     * Valida los datos de registro y lanza excepciones si hay errores.
     */
    private void validateRegisterRequest(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }
    }

    /**
     * Genera un JWT y construye la respuesta para el usuario autenticado.
     */
    private JwtAuthenticationResponse createJwtResponse(User user) {
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .build();
    }
}
