package fr.paymybuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.paymybuddy.model.Transaction;

/**
 * Repository interface for managing {@link Transaction} entities.
 * <p>
 * This repository provides CRUD operations for Transaction objects and
 * additional methods to retrieve transactions based on sender and receiver IDs.
 * </p>
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions sent by a specific user.
     * 
     * @param senderId the ID of the sender user
     * @return a list of transactions where the specified user is the sender
     */
    List<Transaction> findBySender_Id(Long senderId);

    /**
     * Finds all transactions received by a specific user.
     * 
     * @param receiverId the ID of the receiver user
     * @return a list of transactions where the specified user is the receiver
     */
    List<Transaction> findByReceiver_Id(Long receiverId);

}