package com.kayode.paymentservice.controller;

import com.kayode.paymentservice.dto.CustomResponse;
import com.kayode.paymentservice.dto.PaymentRequestDto;
import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Transaction;
import com.kayode.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

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
    public ResponseEntity<CustomResponse<Object>> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        Transaction transaction = paymentService.processPayment(
                paymentRequestDto.getSenderAccountNumber(),
                paymentRequestDto.getReceiverAccountNumber(),
                paymentRequestDto.getAmount()
        );

        CustomResponse<Object> response = new CustomResponse<>();
        response.setStatus(true);
        response.setMessage("Transaction successful");
        response.setData(transaction);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<?> getTransactionStatus(@PathVariable @Pattern(regexp = "\\d+", message = "Transaction ID must be a positive number or a string of digits") String transactionId) {
        Transaction transaction = paymentService.getTransactionStatus(transactionId);

        CustomResponse<Object> response = new CustomResponse<>();
        response.setStatus(true);
        response.setMessage("Transaction found");
        response.setData(transaction);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> getMyAccountBalance() {
        try {
            List<Account> account = paymentService.getAccounts();

            CustomResponse<Object> response = new CustomResponse<>();
            response.setStatus(true);
            response.setMessage("Account(s) fetched successfully");
            response.setData(account);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomResponse<Object> response = new CustomResponse<>();
            response.setStatus(false);
            response.setMessage("Account(s) fetching failed. Error: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Account> getAccountBalance(@PathVariable @Positive(message = "Account ID must be a positive number") Long accountId) {
        Account account = paymentService.getAccountBalance(accountId);
        return ResponseEntity.ok(account);
    }
}
