package com.kayode.paymentservice.service;

import com.kayode.paymentservice.constant.Role;
import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Status;
import com.kayode.paymentservice.model.Transaction;
import com.kayode.paymentservice.model.User;
import com.kayode.paymentservice.repository.AccountRepository;
import com.kayode.paymentservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final AuthenticationService authenticationService;

    public PaymentService(AccountRepository accountRepository, TransactionRepository transactionRepository, AuthenticationService authenticationService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional
    public Transaction processPayment(String receiverAccountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        User senderUser = authenticationService.getAuthenticatedUser();
        Account sender = accountRepository.findByAccountNumber(senderUser.getAccountNumber());
        Account receiver = accountRepository.findByAccountNumber(receiverAccountNumber);

        if (sender == null) {
            throw new IllegalArgumentException("Invalid sender account number");
        }

        if(sender.getStatus().equals(Status.PND)) {
            throw new IllegalArgumentException("Sender account is on PND");
        }

        if(sender.getStatus().equals(Status.INACTIVE)) {
            throw new IllegalArgumentException("Sender account is inactive");
        }

        if (receiver == null) {
            throw new IllegalArgumentException("Invalid receiver account number");
        }

        if(sender.equals(receiver)) {
            throw new IllegalArgumentException("Sender and Recipient can't be the same.");
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
        transaction.setTransactionRef(generateTransactionRef(sender.getId(), receiver.getId()));
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(Status.COMPLETED);

        accountRepository.save(sender);
        accountRepository.save(receiver);
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionStatus(String transactionId) {
        Optional<Transaction> transaction = Optional.empty();
    
        // Check if transactionId is numeric
        if (transactionId.matches("\\d+") && transactionId.length() < 21) {
            // Parse it as Long
            Long idAsLong = Long.parseLong(transactionId);
            
            if (idAsLong <= 0) {
                throw new IllegalArgumentException("Transaction ID must be a positive number");
            }
            
            // Try to find transaction by numeric ID
            transaction = transactionRepository.findById(idAsLong);
        }
    
        // If transaction is not found by numeric ID, search by transaction reference
        if (!transaction.isPresent()) {
            transaction = transactionRepository.findByTransactionRef(transactionId);
        }
    
        // If still not found, throw exception
        if (!transaction.isPresent()) {
            throw new RuntimeException("Invalid transaction ID or reference. Transaction not found.");
        }
    
        return transaction.get();
    }    

    public List<Account> getAccounts() {
        // Get the authenticated user
        User user = this.authenticationService.getAuthenticatedUser();

        if(user.getRole().equals(Role.ROLE_ADMIN) || user.getRole().equals("ROLE_ADMIN")) { //these two checks were added because using Basic Auth, Role is a String and not ENUM
            //get all accounts
            return this.accountRepository.findAll();
        } else {
            //get account details
            Optional<Account> account = this.accountRepository.findByUsername(user.getUsername());

            if(account.isPresent()) {
                return List.of(account.get());
            }
        }
        
        // If no account found for the auth user, throw an exception
        throw new RuntimeException("Account not found for the authenticated user");
    }

    public Account getAccountBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
    }

    protected String generateTransactionRef(Long senderId, Long receiverId) {
        // Get the current timestamp in milliseconds
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // Generate a 6-digit random number
        int randomDigits = ThreadLocalRandom.current().nextInt(100000, 1000000);
        
        // Concatenate timestamp, sender ID, receiver ID, and random digits
        String transactionRef = timestamp + senderId + receiverId + randomDigits;
        
        return transactionRef;
    }
}
