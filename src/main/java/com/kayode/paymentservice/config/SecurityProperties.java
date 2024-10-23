package com.kayode.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private Admin admin = new Admin();
    private User user1 = new User();
    private User user2 = new User();

    // Getters and Setters

    public static class Admin {
        private String username;
        private String password;
        private String role;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class User {
        private String username;
        private String password;
        private String role;
        private String accountNumber;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    }

    // Getters for Admin and Users
    public Admin getAdmin() { return admin; }
    public User getUser1() { return user1; }
    public User getUser2() { return user2; }
}

