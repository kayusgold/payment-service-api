package com.kayode.paymentservice.controller;

import com.kayode.paymentservice.dto.PaymentRequestDto;
import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Transaction;
import com.kayode.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<Transaction> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        Transaction transaction = paymentService.processPayment(
                paymentRequestDto.getSenderAccountNumber(),
                paymentRequestDto.getReceiverAccountNumber(),
                paymentRequestDto.getAmount()
        );
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<Transaction> getTransactionStatus(@PathVariable @Positive(message = "Transaction ID must be a positive number") Long transactionId) {
        Transaction transaction = paymentService.getTransactionStatus(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Account> getAccountBalance(@PathVariable @Positive(message = "Account ID must be a positive number") Long accountId) {
        Account account = paymentService.getAccountBalance(accountId);
        return ResponseEntity.ok(account);
    }
}
