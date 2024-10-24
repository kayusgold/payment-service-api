package com.kayode.paymentservice.repository;

import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    //find by account number
    Account findByAccountNumber(String accountNumber);

    //exist by account number
    boolean existsByAccountNumber(String accountNumber);

    //find by user
    Optional<Account> findByUsername(String username);
}
