package com.kayode.paymentservice.service;

import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Status;
import com.kayode.paymentservice.model.Transaction;
import com.kayode.paymentservice.repository.AccountRepository;
import com.kayode.paymentservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    public PaymentService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction processPayment(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) {
        Account sender = accountRepository.findByAccountNumber(senderAccountNumber);
        Account receiver = accountRepository.findByAccountNumber(receiverAccountNumber);

        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Invalid account numbers");
        }

        if(sender.getStatus().equals(Status.PND) || sender.getStatus().equals(Status.INACTIVE)) {
            throw new IllegalArgumentException("Sender account is " + (sender.getStatus().equals(Status.PND) ? "on PND" : "inactive"));
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        //In the case where all transaction requests must be recorded, population and saving of transaction model below
        //can be moved before balance check and then update statuses according to the condition met
        //e.g insufficient - set status to failed and save
        //e.g success - set status to complete and save
        //Also, the update part of the transactions can be moved to background service and taken outside Transactional flow.
        //This will reduce the processing time when the transactions table has grown large e.g in billions. Other optimization techniques can be applied though.
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(Status.COMPLETED);

        accountRepository.save(sender);
        accountRepository.save(receiver);
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionStatus(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID"));
    }

    public Account getAccountBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
    }
}
