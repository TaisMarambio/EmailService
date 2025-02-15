package com.challenge.emailservice.security;

import com.challenge.emailservice.model.User;
import com.challenge.emailservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Buscando usuario con username: {}", username);

        //search usuario por username
        User userDetail = userRepository.findByUsername(username);

        if (userDetail == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con username: " + username);
        }

        // convertimos rol del usuario en GrantedAuthority
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userDetail.getRole()) // prefijo "ROLE_"
        );

        return new org.springframework.security.core.userdetails.User(
                userDetail.getUsername(),
                userDetail.getPassword(),
                authorities
        );
    }
}
