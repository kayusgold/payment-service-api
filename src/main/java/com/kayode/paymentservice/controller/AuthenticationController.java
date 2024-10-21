package com.kayode.paymentservice.controller;

import com.kayode.paymentservice.config.JwtUtil;
import com.kayode.paymentservice.dto.AuthenticationRequestDto;
import com.kayode.paymentservice.dto.AuthenticationResponseDto;
import com.kayode.paymentservice.service.MyUserDetailsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;

    private final MyUserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, MyUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?>  createAuthenticationToken(@Valid @RequestBody AuthenticationRequestDto request) throws AuthenticationException {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//        );
//
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
//        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
//
//        return jwt;

        try {
            logger.debug("Attempting to authenticate user: {}", request.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            logger.debug("Authentication successful. Loading user details.");
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            logger.debug("Generating JWT token.");
            final String jwt = jwtUtil.generateToken(userDetails.getUsername());

            logger.debug("Returning JWT token in response.");
            return ResponseEntity.ok(new AuthenticationResponseDto(jwt));
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }
}
