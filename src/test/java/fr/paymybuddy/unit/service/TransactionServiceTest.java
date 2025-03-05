package fr.paymybuddy.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.paymybuddy.dto.TransactionResponseDTO;
import fr.paymybuddy.mapper.TransactionMapper;
import fr.paymybuddy.model.Transaction;
import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.TransactionRepository;
import fr.paymybuddy.service.TransactionService;
import fr.paymybuddy.service.UserService;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
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
    private List<Transaction> sentTransactions;
    private List<Transaction> receivedTransactions;

    @BeforeEach
    public void setup() {
        // Création des utilisateurs de test
        testSender = new User();
        testSender.setId(1L);
        testSender.setEmail("sender@example.com");
        testSender.setUsername("sender");
        testSender.setBalance(500.0);

        testReceiver = new User();
        testReceiver.setId(2L);
        testReceiver.setEmail("receiver@example.com");
        testReceiver.setUsername("receiver");
        testReceiver.setBalance(200.0);

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

        // Création des listes de transactions
        sentTransactions = new ArrayList<>();
        sentTransactions.add(testTransaction);

        receivedTransactions = new ArrayList<>();
        Transaction receivedTransaction = new Transaction();
        receivedTransaction.setId(2L);
        receivedTransaction.setSender(testReceiver);
        receivedTransaction.setReceiver(testSender);
        receivedTransaction.setAmount(50.0);
        receivedTransaction.setDescription("Received transaction");
        receivedTransaction.setCreatedAt(LocalDateTime.now().minusDays(1));
        receivedTransactions.add(receivedTransaction);
    }

    @Test
    @DisplayName("Devrait récupérer les transactions filtrées par utilisateur")
    public void testGetFilteredTransactionsByUser() {
        // Given
        when(transactionRepository.findBySender_Id(testSender.getId())).thenReturn(sentTransactions);
        when(transactionRepository.findByReceiver_Id(testSender.getId())).thenReturn(receivedTransactions);
        when(transactionMapper.toTransactionResponseDTO(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction transaction = invocation.getArgument(0);
                    TransactionResponseDTO dto = new TransactionResponseDTO();
                    dto.setSenderId(transaction.getSender().getId());
                    dto.setReceiverId(transaction.getReceiver().getId());
                    dto.setAmount(transaction.getAmount());
                    dto.setDescription(transaction.getDescription());
                    return dto;
                });

        // When
        List<TransactionResponseDTO> result = transactionService.getFilteredTransactionsByUser(testSender);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findBySender_Id(testSender.getId());
        verify(transactionRepository, times(1)).findByReceiver_Id(testSender.getId());
        verify(transactionMapper, times(2)).toTransactionResponseDTO(any(Transaction.class));
    }

    @Test
    @DisplayName("Devrait ajouter une nouvelle transaction")
    public void testAddNewTransaction() {
        // Given
        when(userService.getUserById(testSender.getId())).thenReturn(testSender);
        when(userService.getUserByEmail(testReceiver.getEmail())).thenReturn(testReceiver);
        when(transactionMapper.toTransaction(any(TransactionResponseDTO.class))).thenReturn(testTransaction);
        doNothing().when(userService).updateBalance(any(User.class), any(User.class), anyDouble());

        // When
        transactionService.addNewTransaction(
                testSender.getId(),
                testReceiver.getEmail(),
                "Test transaction",
                100.0);

        // Then
        verify(userService, times(1)).getUserById(testSender.getId());
        verify(userService, times(2)).getUserByEmail(testReceiver.getEmail());
        verify(userService, times(1)).updateBalance(testSender, testReceiver, 100.0);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
