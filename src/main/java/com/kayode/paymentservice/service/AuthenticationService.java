package com.kayode.paymentservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.kayode.paymentservice.config.SecurityProperties;
import com.kayode.paymentservice.model.User;

@Service
public class AuthenticationService {
    private final SecurityProperties securityProperties;

    public AuthenticationService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = extractUsername(authentication);
        return findUserByUsername(username);
    }

    private String extractUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    private User findUserByUsername(String username) {
        // Check admin
        if (username.equals(securityProperties.getAdmin().getUsername())) {
            return User.builder()
                    .username(securityProperties.getAdmin().getUsername())
                    .role(securityProperties.getAdmin().getRole())
                    .build();
        }

        // Check user1
        if (username.equals(securityProperties.getUser1().getUsername())) {
            return User.builder()
                    .username(securityProperties.getUser1().getUsername())
                    .role(securityProperties.getUser1().getRole())
                    .accountNumber(securityProperties.getUser1().getAccountNumber())
                    .build();
        }

        // Check user2
        if (username.equals(securityProperties.getUser2().getUsername())) {
            return User.builder()
                    .username(securityProperties.getUser2().getUsername())
                    .role(securityProperties.getUser2().getRole())
                    .accountNumber(securityProperties.getUser2().getAccountNumber())
                    .build();
        }

        throw new RuntimeException("User not found: " + username);
    }
}