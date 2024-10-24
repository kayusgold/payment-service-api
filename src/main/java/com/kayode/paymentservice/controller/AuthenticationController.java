package com.kayode.paymentservice.controller;

import com.kayode.paymentservice.dto.AuthenticationRequestDto;
import com.kayode.paymentservice.dto.AuthenticationResponseDto;
import com.kayode.paymentservice.dto.CustomResponse;
import com.kayode.paymentservice.model.User;
import com.kayode.paymentservice.service.AuthenticationService;
import com.kayode.paymentservice.service.JwtService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

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
//    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
//
//    private final JwtService jwtService;
//
//    private final AuthenticationService authenticationService;
//
//    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
//        this.jwtService = jwtService;
//        this.authenticationService = authenticationService;
//    }
//
//    @PostMapping("/authenticate")
//    public ResponseEntity<CustomResponse<?>>  createAuthenticationToken(@Valid @RequestBody AuthenticationRequestDto request) throws AuthenticationException {
//        try {
//            logger.debug("Attempting to authenticate user: {}", request.getUsername());
//
//            User authenticatedUser = authenticationService.authenticate(request);
//            String jwtToken = jwtService.generateToken(authenticatedUser);
//            Map<String, String> keyValueMap = new HashMap<>();
//            keyValueMap.put("token", jwtToken);
//
//            CustomResponse<Object> response = new CustomResponse<>();
//            response.setStatus(true);
//            response.setMessage("Login successful");
//            response.setData(keyValueMap);
//
//            return ResponseEntity.ok(response);
//        } catch (AuthenticationException e) {
//            logger.warn("Authentication failed for user: {}", request.getUsername(), e);
//
//            CustomResponse<Object> response = new CustomResponse<>();
//            response.setStatus(false);
//            response.setMessage("Login failed");
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
}
