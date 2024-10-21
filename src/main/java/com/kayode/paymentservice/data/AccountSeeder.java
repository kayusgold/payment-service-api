package com.kayode.paymentservice.data;

import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Status;
import com.kayode.paymentservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class AccountSeeder {

    @Value("${app.security.user1.username}")
    private String user1Username;
    @Value("${app.security.user1.accountNumber}")
    private String user1AccountNumber;

    @Value("${app.security.user2.username}")
    private String user2Username;
    @Value("${app.security.user2.accountNumber}")
    private String user2AccountNumber;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    CommandLineRunner run() {
        return args -> {
            Account user1 = new Account();
            user1.setUsername(user1Username);
            user1.setAccountNumber(user1AccountNumber);
            user1.setBalance(BigDecimal.valueOf(100_000_000));
            user1.setStatus(Status.ACTIVE);

            Account user2 = new Account();
            user2.setUsername(user2Username);
            user2.setAccountNumber(user2AccountNumber);
            user2.setBalance(BigDecimal.valueOf(100_000_000));
            user2.setStatus(Status.ACTIVE);

            accountRepository.save(user1);
            accountRepository.save(user2);
        };
    }
}
