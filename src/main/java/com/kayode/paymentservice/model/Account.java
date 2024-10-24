package com.kayode.paymentservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String accountNumber;
    //We could do available and ledger balances but for simplicity, we will just use balance
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private Status status;
    public Account() {}
    public Account(Long id, String username, String accountNumber, BigDecimal balance, Status status) {
        this.id = id;
        this.username = username;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
