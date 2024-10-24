package com.kayode.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityProperties securityProperties;
    private final BCryptPasswordEncoder passwordEncoder;

    public SecurityConfiguration(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // Create admin user
        UserDetails admin = User.builder()
                .username(securityProperties.getAdmin().getUsername())
                .password(passwordEncoder.encode(securityProperties.getAdmin().getPassword()))
                .roles(securityProperties.getAdmin().getRole().replace("ROLE_", ""))
                .build();

        // Create user1
        UserDetails user1 = User.builder()
                .username(securityProperties.getUser1().getUsername())
                .password(passwordEncoder.encode(securityProperties.getUser1().getPassword()))
                .roles(securityProperties.getUser1().getRole().replace("ROLE_", ""))
                .build();

        // Create user2
        UserDetails user2 = User.builder()
                .username(securityProperties.getUser2().getUsername())
                .password(passwordEncoder.encode(securityProperties.getUser2().getPassword()))
                .roles(securityProperties.getUser2().getRole().replace("ROLE_", ""))
                .build();

        return new InMemoryUserDetailsManager(admin, user1, user2);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/h2/**")
                        .disable())
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/h2/**", "/doc/**", "/favicon.ico")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .httpBasic(Customizer.withDefaults())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}