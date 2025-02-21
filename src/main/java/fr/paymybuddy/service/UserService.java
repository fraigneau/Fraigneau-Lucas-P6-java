package fr.paymybuddy.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.paymybuddy.dto.UserUpdateDTO;
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
                .orElseThrow(() -> new RuntimeException("User byEmail not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User byId not found"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void updateUser(UserUpdateDTO updatedUser, Long id) {

        User user = getUserByEmail(updatedUser.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user = mergeUpdateUser(user, updatedUser);

        userRepository.save(user);
    }

    public User mergeUpdateUser(User existingUser, UserUpdateDTO updateDTO) {
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
}
