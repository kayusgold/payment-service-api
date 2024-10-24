package com.kayode.paymentservice.repository;

import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findByAccountNumber_ExistingAccount_ReturnsAccount() {
        // Arrange
        Account account = new Account();
        account.setUsername("user1");
        account.setAccountNumber("1234567890");
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(Status.ACTIVE);
        entityManager.persist(account);
        entityManager.flush();

        // Act
        Account found = accountRepository.findByAccountNumber("1234567890");

        // Assert
        assertNotNull(found);
        assertEquals("1234567890", found.getAccountNumber());
        assertEquals("user1", found.getUsername());
    }

    @Test
    void findByUsername_ExistingUsername_ReturnsAccount() {
        // Arrange
        Account account = new Account();
        account.setUsername("user2");
        account.setAccountNumber("0987654321");
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(Status.ACTIVE);
        entityManager.persist(account);
        entityManager.flush();

        // Act
        Optional<Account> found = accountRepository.findByUsername("user2");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("user2", found.get().getUsername());
        assertEquals("0987654321", found.get().getAccountNumber());
    }

    @Test
    void findByUsername_NonExistingUsername_ReturnsEmptyOptional() {
        // Act
        Optional<Account> found = accountRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(found.isPresent());
    }
}
