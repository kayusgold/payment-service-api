package com.kayode.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayode.paymentservice.dto.PaymentRequestDto;
import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Transaction;
import com.kayode.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void processPayment_Success() throws Exception {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setReceiverAccountNumber("0987654321");
        requestDto.setAmount(new BigDecimal("100.00"));

        Transaction mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setAmount(new BigDecimal("100.00")); // This is in naira

        when(paymentService.processPayment(anyString(), any(BigDecimal.class))).thenReturn(mockTransaction);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Transaction successful"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.amount").value("100.0")); // Expect 100.0 naira
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void getTransactionStatus_Success() throws Exception {
        Transaction mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setAmount(new BigDecimal("100.00")); // This is in naira

        when(paymentService.getTransactionStatus(anyString())).thenReturn(mockTransaction);

        mockMvc.perform(get("/api/v1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Transaction found"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.amount").value("100.0")); // Expect 100.00 naira
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void getMyAccountBalance_Success() throws Exception {
        Account mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setBalance(new BigDecimal("1000.00")); // This is in naira

        when(paymentService.getAccounts()).thenReturn(Collections.singletonList(mockAccount));

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Account(s) fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].balance").value("1000.0")); // Expect 1000.00 naira
    }
}
