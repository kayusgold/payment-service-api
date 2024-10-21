package com.kayode.paymentservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PaymentRequestDto {

    @NotBlank(message = "Sender account number is required")
    @Pattern(regexp = "\\d{10}", message = "Sender account number must be a 10-digit number")
    private String senderAccountNumber;

    @NotBlank(message = "Receiver account number is required")
    @Pattern(regexp = "\\d{10}", message = "Receiver account number must be a 10-digit number")
    private String receiverAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    // Getters and Setters
    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

