package com.kayode.paymentservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String username;
    private String role;
    private String accountNumber;  // Only for regular users, not admin
}