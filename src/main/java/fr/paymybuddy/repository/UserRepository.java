package fr.paymybuddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.paymybuddy.model.User;

/**
 * Repository interface for managing {@link User} entities.
 * <p>
 * This repository provides CRUD operations for User objects and
 * additional methods to retrieve users based on email and to fetch users
 * with their associated friends.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the user if found, or an empty Optional if not
     *         found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by ID and eagerly fetches their friends.
     * <p>
     * This method uses a join fetch to retrieve the user and their associated
     * friends
     * in a single query, avoiding the N+1 select problem.
     * </p>
     * 
     * @param id the ID of the user to find
     * @return an Optional containing the user with their friends if found, or an
     *         empty Optional if not found
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.friends WHERE u.id = :id")
    Optional<User> findByIdWithFriends(@Param("id") Long id);
}