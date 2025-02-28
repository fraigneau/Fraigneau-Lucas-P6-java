package fr.paymybuddy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.paymybuddy.dto.UserFormDTO;
import fr.paymybuddy.dto.UserFriendDTO;
import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User byEmail not found"));
    }

    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User byId not found"));

        return user;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void updateUser(UserFormDTO updatedUser, Long id) {

        User user = getUserByEmail(updatedUser.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user = mergeUpdateUser(user, updatedUser);

        userRepository.save(user);
    }

    public User mergeUpdateUser(User existingUser, UserFormDTO updateDTO) {
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

    private boolean isValidUpdateUser(String newValue, String existingValue) {
        return newValue != null && !newValue.isBlank() && !newValue.equals(existingValue);
    }

    @Transactional
    public List<UserFriendDTO> getFriends(Long userId) {
        User user = userRepository.findByIdWithFriends(userId)
                .orElseThrow(() -> new RuntimeException("User with Friend not found"));

        return user.getFriends().stream()
                .map(friend -> new UserFriendDTO(friend.getId(), friend.getUsername(), friend.getEmail()))
                .toList();
    }

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
            throw new IllegalArgumentException("User is already friend with this user");
        }
    }

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
            throw new IllegalArgumentException("User is not friend with this user");
        }
    }

    private boolean isFriend(User user, User friend) {
        return user.getFriends().contains(friend) || friend.getFriends().contains(user);
    }

    @Transactional
    public void updateBalance(User sender, User reciver, double amount) {
        if (!isValidBalanceOperationForSender(sender, amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        if (!isValidReceiver(sender, reciver)) {
            throw new IllegalArgumentException("Sender and receiver are the same");
        }
        sender.setBalance(sender.getBalance() - amount);
        reciver.setBalance(sender.getBalance() + amount);
        userRepository.save(sender);
        userRepository.save(reciver);

    }

    private boolean isValidBalanceOperationForSender(User user, double amount) {
        return user.getBalance() - amount > 0 && amount > 0;
    }

    private boolean isValidReceiver(User sender, User receiver) {
        return sender.getId() != receiver.getId();
    }

    @Transactional
    public void addBalance(User user, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }
}
