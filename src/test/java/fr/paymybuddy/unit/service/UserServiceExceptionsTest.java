package fr.paymybuddy.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.paymybuddy.dto.UserFormRequestDTO;
import fr.paymybuddy.exception.ContactAlreadyExistException;
import fr.paymybuddy.exception.DuplicateResourceException;
import fr.paymybuddy.exception.InsufficientBalanceException;
import fr.paymybuddy.exception.ResourceNotFoundException;
import fr.paymybuddy.exception.SelfSendingAmountException;
import fr.paymybuddy.exception.UserNotFondExeption;
import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.UserRepository;
import fr.paymybuddy.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceExceptionsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User friendUser;
    private UserFormRequestDTO userFormRequestDTO;

    @BeforeEach
    public void setup() {
        // Création d'un utilisateur de test
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setBalance(100.0);
        testUser.setFriends(new ArrayList<>());

        // Création d'un ami de test
        friendUser = new User();
        friendUser.setId(2L);
        friendUser.setEmail("friend@example.com");
        friendUser.setUsername("frienduser");
        friendUser.setPassword("encodedPassword");
        friendUser.setBalance(200.0);
        friendUser.setFriends(new ArrayList<>());

        // Création d'un DTO pour les mises à jour
        userFormRequestDTO = new UserFormRequestDTO();
        userFormRequestDTO.setEmail("updated@example.com");
        userFormRequestDTO.setUsername("updateduser");
        userFormRequestDTO.setPassword("newpassword");
    }

    @Test
    @DisplayName("Devrait lancer UserNotFondExeption quand l'utilisateur n'est pas trouvé par email")
    public void testUserNotFoundExceptionByEmail() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When & Then
        UserNotFondExeption exception = assertThrows(UserNotFondExeption.class, () -> {
            userService.getUserByEmail(nonExistentEmail);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer ResourceNotFoundException quand l'utilisateur n'est pas trouvé par ID")
    public void testResourceNotFoundExceptionById() {
        // Given
        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(nonExistentId);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer UserNotFondExeption quand la liste d'amis est demandée pour un utilisateur inexistant")
    public void testUserNotFoundExceptionForFriends() {
        // Given
        Long nonExistentId = 999L;
        when(userRepository.findByIdWithFriends(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFondExeption exception = assertThrows(UserNotFondExeption.class, () -> {
            userService.getFriends(nonExistentId);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer DuplicateResourceException quand on tente d'ajouter un ami déjà existant")
    public void testDuplicateResourceExceptionForAddFriend() {
        // Given
        testUser.getFriends().add(friendUser);
        friendUser.getFriends().add(testUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(friendUser));

        // When & Then
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            userService.addFriend(1L, 2L);
        });

        assertEquals("Relation already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer ContactAlreadyExistException quand on tente de supprimer un ami inexistant")
    public void testContactAlreadyExistExceptionForDeleteFriend() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(friendUser));

        // When & Then
        ContactAlreadyExistException exception = assertThrows(ContactAlreadyExistException.class, () -> {
            userService.deleteFriend(1L, 2L);
        });

        assertEquals("Relation don't exists", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer InsufficientBalanceException quand le solde est insuffisant")
    public void testInsufficientBalanceExceptionForUpdateBalance() {
        // Given
        double amount = 150.0; // Plus que le solde de testUser (100.0)

        // When & Then
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            userService.updateBalance(testUser, friendUser, amount);
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer InsufficientBalanceException quand le montant est négatif")
    public void testInsufficientBalanceExceptionForNegativeAmount() {
        // Given
        double amount = -50.0;

        // When & Then
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            userService.updateBalance(testUser, friendUser, amount);
        });

        assertEquals("Amount must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer SelfSendingAmountException quand l'émetteur et le destinataire sont identiques")
    public void testSelfSendingAmountException() {
        // Given
        double amount = 50.0;

        // When & Then
        SelfSendingAmountException exception = assertThrows(SelfSendingAmountException.class, () -> {
            userService.updateBalance(testUser, testUser, amount);
        });

        assertEquals("Same sander and receiver", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer InsufficientBalanceException quand on tente d'ajouter un solde négatif")
    public void testInsufficientBalanceExceptionForAddBalance() {
        // Given
        double amount = -50.0;

        // When & Then
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            userService.addBalance(testUser, amount);
        });

        assertEquals("Amount must be positive", exception.getMessage());
    }
}