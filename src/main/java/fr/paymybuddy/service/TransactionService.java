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

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private TransactionMapper transMapper;
    private UserService userService;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transMapper,
            UserService userService) {
        this.transactionRepository = transactionRepository;
        this.transMapper = transMapper;
        this.userService = userService;
    }

    private void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    private List<Transaction> getTransactionsBySender(Long sender) {
        return transactionRepository.findBySender_Id(sender);
    }

    private List<Transaction> getTransactionsByReceiver(Long receiver) {
        return transactionRepository.findByReceiver_Id(receiver);
    }

    public List<TransactionResponseDTO> getFilteredTransactionsByUser(User user) {
        List<Transaction> sentTransactions = getTransactionsBySender(user.getId());
        List<Transaction> receivedTransactions = getTransactionsByReceiver(user.getId());

        List<Transaction> allTransactions = addTransactions(sentTransactions, receivedTransactions);
        sortedTransactionsByTime(allTransactions);
        return transactionToDTO(allTransactions);
    }

    private List<TransactionResponseDTO> transactionToDTO(List<Transaction> transactions) {
        return transactions.stream()
                .map(transaction -> transMapper.toTransactionResponseDTO(transaction))
                .toList();
    }

    private List<Transaction> addTransactions(List<Transaction> sent, List<Transaction> received) {
        sent.addAll(received);
        return sent;
    }

    private List<Transaction> sortedTransactionsByTime(List<Transaction> transactions) {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .toList();
    }

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

    @Transactional
    public void addNewTransaction(Long senderId, String email, String desc, double amount) {

        userService.updateBalance(userService.getUserById(senderId), userService.getUserByEmail(email), amount);
        Transaction transaction = setTransaction(email, desc, amount, senderId);
        transaction.setCreatedAt(LocalDateTime.now());
        saveTransaction(transaction);
    }
}