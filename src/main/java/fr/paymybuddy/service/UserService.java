package fr.paymybuddy.service;

import org.springframework.stereotype.Service;

import fr.paymybuddy.dto.UserUpdateDTO;
import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        user = mergeUserUpdate(user, updatedUser);

        userRepository.save(user);
    }

    public User mergeUserUpdate(User existingUser, UserUpdateDTO updateDTO) {
        if (existingUser == null || updateDTO == null) {
            throw new IllegalArgumentException(
                    "les donnee de mise ajour sont null");
        }

        if (updateDTO.getUsername() != null && !updateDTO.getUsername().isBlank() &&
                !updateDTO.getUsername().equals(existingUser.getUsername())) {
            existingUser.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank() &&
                !updateDTO.getEmail().equals(existingUser.getEmail())) {
            existingUser.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isBlank()) {
            // TODO HASH PASSWORD
            existingUser.setPassword(updateDTO.getPassword());
        }

        return existingUser;
    }
}
