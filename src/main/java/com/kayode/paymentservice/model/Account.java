package com.kayode.paymentservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true)
    private String accountNumber;

    //We could do available and ledger balances but for simplicity, we will just use balance
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Account() {}

    public Account(Long id, User user, String accountNumber, BigDecimal balance, Status status) {
        this.id = id;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        // When retrieving the balance, convert it from kobo to the actual balance by dividing by 100
        return balance != null ? balance.divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
    }

    public void setBalance(BigDecimal balance) {
        // When setting the balance, convert it to kobo by multiplying by 100
        this.balance = balance != null ? balance.multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
