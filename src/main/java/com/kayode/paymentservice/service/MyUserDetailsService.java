package com.kayode.paymentservice.service;

import com.kayode.paymentservice.config.PasswordEncoderConfig;
import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Value("${app.security.user1.username}")
    private String user1Username;

    @Value("${app.security.user1.password}")
    private String user1Password;

    @Value("${app.security.user1.role}")
    private String user1Role;

    @Value("${app.security.user2.username}")
    private String user2Username;

    @Value("${app.security.user2.password}")
    private String user2Password;

    @Value("${app.security.user2.role}")
    private String user2Role;

    private final PasswordEncoder passwordEncoder;

    public MyUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if user matches User 1 or User 2 from application.properties
        String password;
        String role;

        if (username.equals(user1Username)) {
            password = user1Password;
            role = user1Role;
        } else if (username.equals(user2Username)) {
            password = user2Password;
            role = user2Role;
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return User.builder()
                .username(username)
                .password(this.passwordEncoder.encode(password))
                .roles(role)
                .build();
    }
}
