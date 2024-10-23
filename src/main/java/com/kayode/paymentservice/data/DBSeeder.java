package com.kayode.paymentservice.data;

import com.kayode.paymentservice.config.SecurityProperties;
import com.kayode.paymentservice.constant.Role;
import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Status;
import com.kayode.paymentservice.model.User;
import com.kayode.paymentservice.repository.AccountRepository;
import com.kayode.paymentservice.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DBSeeder {

    // @Value("${app.security.user1.username}")
    // private String user1Username;
    // @Value("${app.security.user1.accountNumber}")
    // private String user1AccountNumber;

    // @Value("${app.security.user2.username}")
    // private String user2Username;
    // @Value("${app.security.user2.accountNumber}")
    // private String user2AccountNumber;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    CommandLineRunner run() {
        return args -> {

            User admin = new User();
            admin.setFullName(securityProperties.getAdmin().getUsername());
            admin.setUsername(securityProperties.getAdmin().getUsername());
            admin.setPassword(bCryptPasswordEncoder.encode(securityProperties.getAdmin().getPassword()));
            admin.setRole(securityProperties.getAdmin().getRole().equals("ROLE_ADMIN") ? Role.ROLE_ADMIN : Role.ROLE_USER);
            userRepository.save(admin);

            User user1 = new User();
            user1.setFullName(securityProperties.getUser1().getUsername());
            user1.setUsername(securityProperties.getUser1().getUsername());
            user1.setPassword(bCryptPasswordEncoder.encode(securityProperties.getUser1().getPassword()));
            user1.setRole(securityProperties.getUser1().getRole().equals("ROLE_ADMIN") ? Role.ROLE_ADMIN : Role.ROLE_USER);
            userRepository.save(user1);

            Account account1 = new Account();
            account1.setUser(user1);
            account1.setAccountNumber(securityProperties.getUser1().getAccountNumber());
            account1.setBalance(BigDecimal.valueOf(100_000_000));
            account1.setStatus(Status.ACTIVE);
            accountRepository.save(account1);

            User user2 = new User();
            user2.setFullName(securityProperties.getUser2().getUsername());
            user2.setUsername(securityProperties.getUser2().getUsername());
            user2.setPassword(bCryptPasswordEncoder.encode(securityProperties.getUser2().getPassword()));
            user2.setRole(securityProperties.getUser2().getRole().equals("ROLE_ADMIN") ? Role.ROLE_ADMIN : Role.ROLE_USER);
            userRepository.save(user2);

            Account account2 = new Account();
            account2.setUser(user2);
            account2.setAccountNumber(securityProperties.getUser2().getAccountNumber());
            account2.setBalance(BigDecimal.valueOf(100_000_000));
            account2.setStatus(Status.ACTIVE);
            accountRepository.save(account2);
        };
    }
}
