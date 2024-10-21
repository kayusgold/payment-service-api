package com.kayode.paymentservice.config;

import com.kayode.paymentservice.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Value("${app.security.admin.role}")
    private String adminRole;

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

    private final JwtRequestFilter jwtRequestFilter;
//    private final MyUserDetailsService userDetailsService;



    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
//        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user1 = User.builder()
//                .username(user1Username)
//                .password(bCryptPasswordEncoder().encode(user1Password))
//                .authorities("ROLE_USER")
//                .build();
//
//        UserDetails user2 = User.builder()
//                .username(user2Username)
//                .password(bCryptPasswordEncoder().encode(user2Password))
//                .authorities("ROLE_USER")
//                .build();
//
//        UserDetails admin = User.builder()
//                .username(adminUsername)
//                .password(bCryptPasswordEncoder().encode(adminPassword))
//                .authorities("ROLE_ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2, admin);
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(bCryptPasswordEncoder())
//                .and()
//                .build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                            try {
                                req
                                        .requestMatchers("/api/v1/authenticate", "/h2-console/**", "/h2/**", "/doc/**").permitAll() // Exempt H2 Console and documentation paths
                                        .requestMatchers("/api/v1/payments").hasAnyRole("USER", "ADMIN")
                                        .requestMatchers("/api/v1/transactions/**").hasAnyRole("USER", "ADMIN")
                                        .requestMatchers("/api/v1/accounts/**").hasAnyRole("USER", "ADMIN")
                                        .anyRequest().authenticated();

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}