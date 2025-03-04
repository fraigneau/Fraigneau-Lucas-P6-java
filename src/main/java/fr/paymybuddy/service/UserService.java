package fr.paymybuddy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.paymybuddy.dto.UserFormRequestDTO;
import fr.paymybuddy.dto.UserFriendResponseDTO;
import fr.paymybuddy.exception.ContactAlreadyExistException;
import fr.paymybuddy.exception.DuplicateResourceException;
import fr.paymybuddy.exception.InsufficientBalanceException;
import fr.paymybuddy.exception.ResourceNotFoundException;
import fr.paymybuddy.exception.SelfSendingAmountException;
import fr.paymybuddy.exception.UserNotFondExeption;
import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.UserRepository;

/**
 * Service class that manages user-related operations.
 * <p>
 * This service handles user management functionality including user retrieval,
 * creation, updates, friend relationships, and balance operations.
 * </p>
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserService with required dependencies.
     *
     * @param userRepository  repository for User entity operations
     * @param passwordEncoder encoder for securely hashing user passwords
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return the user with the specified email
     * @throws UserNotFondExeption if no user is found with the given email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFondExeption("User not found"));
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user;
    }

    /**
     * Saves an existing user to the database.
     *
     * @param user the user to save
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Creates and saves a new user to the database.
     * <p>
     * This method encodes the user's password before saving.
     * </p>
     *
     * @param user the new user to save
     */
    public void saveNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Updates an existing user's information.
     *
     * @param updatedUser DTO containing the updated user information
     * @param id          the ID of the user to update
     */
    public void updateUser(UserFormRequestDTO updatedUser, Long id) {
        User user = getUserByEmail(updatedUser.getEmail());
        user = mergeUpdateUser(user, updatedUser);
        saveUser(user);
    }

    /**
     * Merges updated user information into an existing user entity.
     * <p>
     * This method only updates fields that have valid new values.
     * </p>
     *
     * @param existingUser the existing user entity to update
     * @param updateDTO    DTO containing the fields to update
     * @return the updated user entity
     */
    public User mergeUpdateUser(User existingUser, UserFormRequestDTO updateDTO) {
        Optional.ofNullable(updateDTO.getUsername())
                .filter(username -> isValidUpdateUser(username, existingUser.getUsername()))
                .ifPresent(existingUser::setUsername);

        Optional.ofNullable(updateDTO.getEmail())
                .filter(email -> isValidUpdateUser(email, existingUser.getEmail()))
                .ifPresent(existingUser::setEmail);

        Optional.ofNullable(updateDTO.getPassword())
                .filter(password -> !password.isBlank())
                .map(passwordEncoder::encode)
                .ifPresent(existingUser::setPassword);

        return existingUser;
    }

    /**
     * Validates if a field value should be updated.
     * <p>
     * A value is considered valid for update if it's not null, not blank,
     * and different from the current value.
     * </p>
     *
     * @param newValue      the new value to validate
     * @param existingValue the current value
     * @return true if the new value is valid for update, false otherwise
     */
    private boolean isValidUpdateUser(String newValue, String existingValue) {
        return newValue != null && !newValue.isBlank() && !newValue.equals(existingValue);
    }

    /**
     * Retrieves a list of friends for a specific user.
     *
     * @param userId the ID of the user whose friends should be retrieved
     * @return a list of DTOs containing information about the user's friends
     * @throws UserNotFondExeption if the user is not found
     */
    @Transactional
    public List<UserFriendResponseDTO> getFriends(Long userId) {
        User user = userRepository.findByIdWithFriends(userId)
                .orElseThrow(() -> new UserNotFondExeption("User not found"));

        return user.getFriends().stream()
                .map(friend -> new UserFriendResponseDTO(friend.getId(), friend.getUsername(), friend.getEmail()))
                .toList();
    }

    /**
     * Adds a bidirectional friend relationship between two users.
     *
     * @param userId   the ID of the first user
     * @param friendId the ID of the second user to add as a friend
     * @throws DuplicateResourceException if the friend relationship already exists
     */
    @Transactional
    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!isFriend(user, friend)) {
            user.getFriends().add(friend);
            friend.getFriends().add(user);
            userRepository.save(user);
            userRepository.save(friend);
        } else {
            throw new DuplicateResourceException("Relation already exists");
        }
    }

    /**
     * Removes a bidirectional friend relationship between two users.
     *
     * @param userId   the ID of the first user
     * @param friendId the ID of the second user to remove from friends
     * @throws ContactAlreadyExistException if the friend relationship doesn't exist
     */
    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (isFriend(user, friend)) {
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);
            userRepository.save(user);
            userRepository.save(friend);
        } else {
            throw new ContactAlreadyExistException("Relation don't exists");
        }
    }

    /**
     * Checks if two users have a friend relationship.
     *
     * @param user   the first user
     * @param friend the second user
     * @return true if the users are friends, false otherwise
     */
    private boolean isFriend(User user, User friend) {
        return user.getFriends().contains(friend) || friend.getFriends().contains(user);
    }

    /**
     * Transfers money from one user to another.
     * <p>
     * Updates the balance of both the sender and receiver after validating
     * that the operation is legitimate.
     * </p>
     *
     * @param sender  the user sending the money
     * @param reciver the user receiving the money
     * @param amount  the amount to transfer
     * @throws InsufficientBalanceException if the sender has insufficient balance
     *                                      or the amount is not positive
     * @throws SelfSendingAmountException   if the sender and receiver are the same
     *                                      user
     */
    @Transactional
    public void updateBalance(User sender, User reciver, double amount) {
        if (!isValidBalanceOperationForSender(sender, amount)) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        if (!isValidReceiver(sender, reciver)) {
            throw new SelfSendingAmountException("Same sander and receiver");
        }
        if (!isPositiveAmount(amount)) {
            throw new InsufficientBalanceException("Amount must be positive");
        }
        sender.setBalance(sender.getBalance() - amount);
        reciver.setBalance(reciver.getBalance() + amount);
        userRepository.save(sender);
        userRepository.save(reciver);
    }

    /**
     * Checks if a user has sufficient balance for a withdrawal operation.
     *
     * @param user   the user whose balance is being checked
     * @param amount the amount to be withdrawn
     * @return true if the user has sufficient balance, false otherwise
     */
    private boolean isValidBalanceOperationForSender(User user, double amount) {
        return user.getBalance() - amount > 0;
    }

    /**
     * Validates that an amount is positive.
     *
     * @param amount the amount to validate
     * @return true if the amount is positive, false otherwise
     */
    private boolean isPositiveAmount(double amount) {
        return amount > 0;
    }

    /**
     * Validates that the sender and receiver are different users.
     *
     * @param sender   the sending user
     * @param receiver the receiving user
     * @return true if sender and receiver are different users, false otherwise
     */
    private boolean isValidReceiver(User sender, User receiver) {
        return sender.getId() != receiver.getId();
    }

    /**
     * Adds funds to a user's balance.
     *
     * @param user   the user whose balance will be increased
     * @param amount the amount to add
     * @throws InsufficientBalanceException if the amount is not positive
     */
    @Transactional
    public void addBalance(User user, double amount) {
        if (!isPositiveAmount(amount)) {
            throw new InsufficientBalanceException("Amount must be positive");
        }
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }
}