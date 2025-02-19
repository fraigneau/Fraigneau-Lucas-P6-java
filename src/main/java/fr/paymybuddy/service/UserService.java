package fr.paymybuddy.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
