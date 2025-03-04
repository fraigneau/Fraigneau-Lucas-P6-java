package fr.paymybuddy.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.paymybuddy.dto.TransactionResponseDTO;
import fr.paymybuddy.mapper.TransactionMapper;
import fr.paymybuddy.model.Transaction;
import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;

/**
 * Service class that handles the business logic for transactions.
 * <p>
 * This service manages operations related to transactions, including creation,
 * retrieval,
 * filtering, and processing of transaction data between users.
 * </p>
 */
@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private TransactionMapper transMapper;
    private UserService userService;

    /**
     * Constructs a new TransactionService with the necessary dependencies.
     *
     * @param transactionRepository repository for Transaction entity operations
     * @param transMapper           mapper for converting between Transaction
     *                              entities and DTOs
     * @param userService           service for user-related operations
     */
    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transMapper,
            UserService userService) {
        this.transactionRepository = transactionRepository;
        this.transMapper = transMapper;
        this.userService = userService;
    }

    /**
     * Persists a transaction to the database.
     *
     * @param transaction the transaction to save
     */
    private void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    /**
     * Retrieves all transactions where the specified user is the sender.
     *
     * @param sender the ID of the sending user
     * @return a list of transactions sent by the specified user
     */
    private List<Transaction> getTransactionsBySender(Long sender) {
        return transactionRepository.findBySender_Id(sender);
    }

    /**
     * Retrieves all transactions where the specified user is the receiver.
     *
     * @param receiver the ID of the receiving user
     * @return a list of transactions received by the specified user
     */
    private List<Transaction> getTransactionsByReceiver(Long receiver) {
        return transactionRepository.findByReceiver_Id(receiver);
    }

    /**
     * Retrieves and filters all transactions related to a specific user.
     * <p>
     * This method combines both sent and received transactions for the user,
     * sorts them by creation time in descending order, and converts them to DTOs.
     * </p>
     *
     * @param user the user whose transactions should be retrieved
     * @return a list of transaction DTOs related to the specified user
     */
    public List<TransactionResponseDTO> getFilteredTransactionsByUser(User user) {
        List<Transaction> sentTransactions = getTransactionsBySender(user.getId());
        List<Transaction> receivedTransactions = getTransactionsByReceiver(user.getId());

        List<Transaction> allTransactions = addTransactions(sentTransactions, receivedTransactions);
        sortedTransactionsByTime(allTransactions);
        return transactionToDTO(allTransactions);
    }

    /**
     * Converts a list of Transaction entities to TransactionResponseDTOs.
     *
     * @param transactions the list of transaction entities to convert
     * @return a list of transaction DTOs
     */
    private List<TransactionResponseDTO> transactionToDTO(List<Transaction> transactions) {
        return transactions.stream()
                .map(transaction -> transMapper.toTransactionResponseDTO(transaction))
                .toList();
    }

    /**
     * Combines two lists of transactions into a single list.
     *
     * @param sent     the list of sent transactions
     * @param received the list of received transactions
     * @return a combined list containing all transactions
     */
    private List<Transaction> addTransactions(List<Transaction> sent, List<Transaction> received) {
        sent.addAll(received);
        return sent;
    }

    /**
     * Sorts a list of transactions by creation time in descending order (newest
     * first).
     *
     * @param transactions the list of transactions to sort
     * @return the sorted list of transactions
     */
    private List<Transaction> sortedTransactionsByTime(List<Transaction> transactions) {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .toList();
    }

    /**
     * Creates a new Transaction entity from the provided information.
     *
     * @param email    the email of the transaction receiver
     * @param desc     the description of the transaction
     * @param amount   the amount of the transaction
     * @param senderId the ID of the sending user
     * @return a new Transaction entity
     */
    private Transaction setTransaction(String email, String desc, double amount, Long senderId) {
        User reciver = userService.getUserByEmail(email);
        TransactionResponseDTO transaction = new TransactionResponseDTO();
        transaction.setReceiverEmail(email);
        transaction.setDescription(desc);
        transaction.setAmount(amount);
        transaction.setSenderId(senderId);
        transaction.setReceiverId(reciver.getId());

        return transMapper.toTransaction(transaction);
    }

    /**
     * Creates and persists a new transaction between users.
     * <p>
     * This method updates the balances of both the sender and receiver,
     * creates a new transaction record with the current timestamp, and saves it.
     * </p>
     *
     * @param senderId the ID of the user sending the money
     * @param email    the email of the user receiving the money
     * @param desc     the description of the transaction
     * @param amount   the amount of money to transfer
     */
    @Transactional
    public void addNewTransaction(Long senderId, String email, String desc, double amount) {
        userService.updateBalance(userService.getUserById(senderId), userService.getUserByEmail(email), amount);
        Transaction transaction = setTransaction(email, desc, amount, senderId);
        transaction.setCreatedAt(LocalDateTime.now());
        saveTransaction(transaction);
    }
}