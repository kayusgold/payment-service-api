package com.kayode.paymentservice.service;

import com.kayode.paymentservice.model.Account;
import com.kayode.paymentservice.model.Status;
import com.kayode.paymentservice.model.Transaction;
import com.kayode.paymentservice.model.User;
import com.kayode.paymentservice.repository.AccountRepository;
import com.kayode.paymentservice.repository.TransactionRepository;
import com.kayode.paymentservice.config.SecurityProperties;
import com.kayode.paymentservice.constant.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SecurityProperties securityProperties;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processPayment_SuccessfulTransaction() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");

        Account sender = new Account();
        sender.setId(1L);
        sender.setAccountNumber("1234567890");
        sender.setBalance(new BigDecimal("1000.00"));
        sender.setStatus(Status.ACTIVE);

        Account receiver = new Account();
        receiver.setId(2L);
        receiver.setAccountNumber("0987654321");
        receiver.setBalance(new BigDecimal("500.00"));
        receiver.setStatus(Status.ACTIVE);

        BigDecimal amount = new BigDecimal("100.00");

        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(sender);
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Transaction result = paymentService.processPayment("0987654321", amount);

        // Assert
        assertNotNull(result);
        assertEquals(Status.COMPLETED, result.getStatus());
        assertEquals(new BigDecimal("900.00"), sender.getBalance());
        assertEquals(new BigDecimal("600.00"), receiver.getBalance());
        verify(accountRepository, times(1)).save(sender);
        verify(accountRepository, times(1)).save(receiver);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void processPayment_InsufficientFunds() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");

        Account sender = new Account();
        sender.setAccountNumber("1234567890");
        sender.setBalance(new BigDecimal("50.00"));
        sender.setStatus(Status.ACTIVE);

        Account receiver = new Account();
        receiver.setAccountNumber("0987654321");
        receiver.setBalance(new BigDecimal("500.00"));
        receiver.setStatus(Status.ACTIVE);

        BigDecimal amount = new BigDecimal("100.00");

        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(sender);
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(receiver);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> paymentService.processPayment("0987654321", amount));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void processPayment_SenderAndReceiverSame_ThrowsException() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");

        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(Status.ACTIVE);

        BigDecimal amount = new BigDecimal("100.00");

        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(account);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("1234567890", amount));
        assertEquals("Sender and Recipient can't be the same.", exception.getMessage());
    }

    @Test
    void processPayment_InvalidSenderAccount_ThrowsException() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");
        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);
        
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("0987654321", new BigDecimal("100.00")));
        assertEquals("Invalid sender account number", exception.getMessage());
    }

    @Test
    void processPayment_InvalidReceiverAccountNumber_ThrowsException() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");
        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);

        Account sender = new Account();
        sender.setAccountNumber("1234567890");
        sender.setBalance(new BigDecimal("1000.00"));
        sender.setStatus(Status.ACTIVE);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(sender);

        when(accountRepository.findByAccountNumber("123456")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("123456", new BigDecimal("100.00")));
        assertEquals("Invalid receiver account number", exception.getMessage());
    }

    @Test
    void processPayment_NonNumericAccountNumber_ThrowsException() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");

        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);

        Account sender = new Account();
        sender.setAccountNumber("1234567890");
        sender.setBalance(new BigDecimal("1000.00"));
        sender.setStatus(Status.ACTIVE);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(sender);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("123456789A", new BigDecimal("100.00")));
        assertEquals("Invalid receiver account number", exception.getMessage());
    }

    @Test
    void processPayment_ZeroAmount_ThrowsException() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");

        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("0987654321", BigDecimal.ZERO));
        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void processPayment_NegativeAmount_ThrowsException() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");
        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);

        Account sender = new Account();
        sender.setAccountNumber("1234567890");
        sender.setBalance(new BigDecimal("1000.00"));
        sender.setStatus(Status.ACTIVE);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(sender);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("0987654321", new BigDecimal("-100.00")));
        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void processPayment_SenderAccountPND_ThrowsException() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");
        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);

        Account sender = new Account();
        sender.setAccountNumber("1234567890");
        sender.setBalance(new BigDecimal("1000.00"));
        sender.setStatus(Status.PND);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(sender);

        Account receiver = new Account();
        receiver.setAccountNumber("0987654321");
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(receiver);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("0987654321", new BigDecimal("100.00")));
        assertEquals("Sender account is on PND", exception.getMessage());
    }

    @Test
    void processPayment_SenderAccountInactive_ThrowsException() {
        // Arrange
        User senderUser = new User();
        senderUser.setAccountNumber("1234567890");

        Account sender = new Account();
        sender.setAccountNumber("1234567890");
        sender.setBalance(new BigDecimal("1000.00"));
        sender.setStatus(Status.INACTIVE);

        when(authenticationService.getAuthenticatedUser()).thenReturn(senderUser);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(sender);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("0987654321", new BigDecimal("100.00")));
        assertEquals("Sender account is inactive", exception.getMessage());
    }

    @Test
    void getTransactionStatus_ValidNumericId_ReturnsTransaction() {
        // Arrange
        Long transactionId = 1L;
        Transaction mockTransaction = new Transaction();
        mockTransaction.setId(transactionId);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(mockTransaction));

        // Act
        Transaction result = paymentService.getTransactionStatus(transactionId.toString());

        // Assert
        assertNotNull(result);
        assertEquals(transactionId, result.getId());
    }

    @Test
    void getTransactionStatus_ValidTransactionRef_ReturnsTransaction() {
        // Arrange
        String transactionRef = "TR123456789";
        Transaction mockTransaction = new Transaction();
        mockTransaction.setTransactionRef(transactionRef);
        when(transactionRepository.findByTransactionRef(transactionRef)).thenReturn(Optional.of(mockTransaction));

        // Act
        Transaction result = paymentService.getTransactionStatus(transactionRef);

        // Assert
        assertNotNull(result);
        assertEquals(transactionRef, result.getTransactionRef());
    }

    @Test
    void getTransactionStatus_InvalidId_ThrowsException() {
        // Arrange
        String invalidId = "invalid";
        when(transactionRepository.findById(any())).thenReturn(Optional.empty());
        when(transactionRepository.findByTransactionRef(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> paymentService.getTransactionStatus(invalidId));
    }

    @Test
    void getAccounts_AdminUser_ReturnsAllAccounts() {
        // Arrange
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setRole("ROLE_ADMIN");  // Set the role
        when(authenticationService.getAuthenticatedUser()).thenReturn(adminUser);

        List<Account> mockAccounts = Arrays.asList(new Account(), new Account());
        when(accountRepository.findAll()).thenReturn(mockAccounts);

        // Act
        List<Account> result = paymentService.getAccounts();

        // Assert
        assertEquals(2, result.size());
        verify(accountRepository).findAll();
    }

    @Test
    void getAccounts_RegularUser_ReturnsUserAccount() {
        // Arrange
        User regularUser = new User();
        regularUser.setUsername("user1");
        regularUser.setRole("ROLE_USER");  // Set the role
        when(authenticationService.getAuthenticatedUser()).thenReturn(regularUser);

        Account mockAccount = new Account();
        mockAccount.setUsername("user1");
        when(accountRepository.findByUsername("user1")).thenReturn(Optional.of(mockAccount));

        // Act
        List<Account> result = paymentService.getAccounts();

        // Assert
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        verify(accountRepository).findByUsername("user1");
    }

    @Test
    void getAccounts_UserNotFound_ThrowsException() {
        // Arrange
        User regularUser = new User();
        regularUser.setUsername("user1");
        when(authenticationService.getAuthenticatedUser()).thenReturn(regularUser);

        SecurityProperties.User userProperties = new SecurityProperties.User();
        userProperties.setRole("ROLE_USER");
        when(securityProperties.getUser1()).thenReturn(userProperties);

        when(accountRepository.findByUsername("user1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> paymentService.getAccounts());
    }

    @Test
    void getAccountBalance_ValidAccountId_ReturnsAccount() {
        // Arrange
        Long accountId = 1L;
        Account mockAccount = new Account();
        mockAccount.setId(accountId);
        mockAccount.setBalance(new BigDecimal("1000.00"));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));

        // Act
        Account result = paymentService.getAccountBalance(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
    }

    @Test
    void getAccountBalance_InvalidAccountId_ThrowsException() {
        // Arrange
        Long invalidAccountId = 999L;
        when(accountRepository.findById(invalidAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> paymentService.getAccountBalance(invalidAccountId));
    }

    @Test
    void generateTransactionRef_UniqueReference() {
        // Arrange
        Long senderId = 1L;
        Long receiverId = 2L;

        // Act
        String result1 = paymentService.generateTransactionRef(senderId, receiverId);
        String result2 = paymentService.generateTransactionRef(senderId, receiverId);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1, result2);
        assertTrue(result1.contains(senderId.toString()));
        assertTrue(result1.contains(receiverId.toString()));
        assertTrue(result2.contains(senderId.toString()));
        assertTrue(result2.contains(receiverId.toString()));
    }
}
