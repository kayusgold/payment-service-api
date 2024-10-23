package com.kayode.paymentservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_ref")
    private String transactionRef;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account sender;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Account receiver;

    private BigDecimal amount;
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Transaction() {}

    public Transaction(Long id, Account sender, Account receiver, BigDecimal amount, LocalDateTime timestamp, Status status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    // These fields will be used to show the sender_id and receiver_id in the JSON response
    @Transient
    @JsonProperty("sender_id")
    public Long getSenderId() {
        return sender != null ? sender.getId() : null;  // Fetch the sender ID
    }

    @Transient
    @JsonProperty("receiver_id")
    public Long getReceiverId() {
        return receiver != null ? receiver.getId() : null;  // Fetch the receiver ID
    }

    public BigDecimal getAmount() {
        //convert to naira
        return amount != null ? amount.divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO;

    }

    public void setAmount(BigDecimal amount) {
        //convert to kobo
        this.amount = amount != null ? amount.multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
