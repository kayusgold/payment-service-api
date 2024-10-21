package com.kayode.paymentservice.repository;

import com.kayode.paymentservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    //find by account number
    Account findByAccountNumber(String accountNumber);
}
