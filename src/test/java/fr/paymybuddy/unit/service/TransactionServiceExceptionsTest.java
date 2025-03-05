package fr.paymybuddy.unit.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.paymybuddy.dto.TransactionResponseDTO;
import fr.paymybuddy.exception.InsufficientBalanceException;
import fr.paymybuddy.exception.SelfSendingAmountException;
import fr.paymybuddy.exception.UserNotFondExeption;
import fr.paymybuddy.mapper.TransactionMapper;
import fr.paymybuddy.model.Transaction;
import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.TransactionRepository;
import fr.paymybuddy.service.TransactionService;
import fr.paymybuddy.service.UserService;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceExceptionsTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    private User testSender;
    private User testReceiver;
    private Transaction testTransaction;
    private TransactionResponseDTO testTransactionDTO;

    @BeforeEach
    public void setup() {
        // Création des utilisateurs de test
        testSender = new User();
        testSender.setId(1L);
        testSender.setEmail("sender@example.com");
        testSender.setUsername("sender");
        testSender.setBalance(500.0);
        testSender.setFriends(new ArrayList<>());

        testReceiver = new User();
        testReceiver.setId(2L);
        testReceiver.setEmail("receiver@example.com");
        testReceiver.setUsername("receiver");
        testReceiver.setBalance(200.0);
        testReceiver.setFriends(new ArrayList<>());

        // Création d'une transaction de test
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setSender(testSender);
        testTransaction.setReceiver(testReceiver);
        testTransaction.setAmount(100.0);
        testTransaction.setDescription("Test transaction");
        testTransaction.setCreatedAt(LocalDateTime.now());

        // Création d'un DTO de transaction
        testTransactionDTO = new TransactionResponseDTO();
        testTransactionDTO.setSenderId(1L);
        testTransactionDTO.setReceiverId(2L);
        testTransactionDTO.setAmount(100.0);
        testTransactionDTO.setDescription("Test transaction");
        testTransactionDTO.setReceiverEmail("receiver@example.com");
    }

    @Test
    @DisplayName("Devrait propager UserNotFondExeption quand le destinataire n'est pas trouvé")
    public void testUserNotFoundExceptionForReceiver() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userService.getUserById(testSender.getId())).thenReturn(testSender);
        when(userService.getUserByEmail(nonExistentEmail)).thenThrow(new UserNotFondExeption("User not found"));

        // When & Then
        assertThrows(UserNotFondExeption.class, () -> {
            transactionService.addNewTransaction(testSender.getId(), nonExistentEmail, "Test transaction", 100.0);
        });
    }

    @Test
    @DisplayName("Devrait propager UserNotFondExeption quand l'expéditeur n'est pas trouvé")
    public void testUserNotFoundExceptionForSender() {
        // Given
        Long nonExistentId = 999L;
        when(userService.getUserById(nonExistentId)).thenThrow(new UserNotFondExeption("User not found"));

        // When & Then
        assertThrows(UserNotFondExeption.class, () -> {
            transactionService.addNewTransaction(nonExistentId, testReceiver.getEmail(), "Test transaction", 100.0);
        });
    }

    @Test
    @DisplayName("Devrait propager InsufficientBalanceException quand le solde est insuffisant")
    public void testInsufficientBalanceException() {
        // Given
        double amount = 600.0; // Plus que le solde de testSender (500.0)
        when(userService.getUserById(testSender.getId())).thenReturn(testSender);
        when(userService.getUserByEmail(testReceiver.getEmail())).thenReturn(testReceiver);
        doThrow(new InsufficientBalanceException("Insufficient balance"))
                .when(userService).updateBalance(any(User.class), any(User.class), anyDouble());

        // When & Then
        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.addNewTransaction(testSender.getId(), testReceiver.getEmail(), "Test transaction",
                    amount);
        });
    }

    @Test
    @DisplayName("Devrait propager SelfSendingAmountException quand l'expéditeur et le destinataire sont identiques")
    public void testSelfSendingAmountException() {
        // Given
        when(userService.getUserById(testSender.getId())).thenReturn(testSender);
        when(userService.getUserByEmail(testSender.getEmail())).thenReturn(testSender);
        doThrow(new SelfSendingAmountException("Same sender and receiver"))
                .when(userService).updateBalance(any(User.class), any(User.class), anyDouble());

        // When & Then
        assertThrows(SelfSendingAmountException.class, () -> {
            transactionService.addNewTransaction(testSender.getId(), testSender.getEmail(), "Test transaction", 100.0);
        });
    }

    @Test
    @DisplayName("Devrait propager InsufficientBalanceException quand le montant est négatif")
    public void testNegativeAmountException() {
        // Given
        double amount = -100.0;
        when(userService.getUserById(testSender.getId())).thenReturn(testSender);
        when(userService.getUserByEmail(testReceiver.getEmail())).thenReturn(testReceiver);
        doThrow(new InsufficientBalanceException("Amount must be positive"))
                .when(userService).updateBalance(any(User.class), any(User.class), anyDouble());

        // When & Then
        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.addNewTransaction(testSender.getId(), testReceiver.getEmail(), "Test transaction",
                    amount);
        });
    }
}