package fr.paymybuddy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import fr.paymybuddy.config.TestSecurityConfig;
import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.TransactionResponseDTO;
import fr.paymybuddy.dto.UserFriendResponseDTO;
import fr.paymybuddy.mapper.TransactionMapper;
import fr.paymybuddy.model.User;
import fr.paymybuddy.service.TransactionService;
import fr.paymybuddy.service.UserService;

@WebMvcTest(TransactionController.class)
@Import(TestSecurityConfig.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private TransactionMapper transactionMapper;

    private User testUser;
    private UserDetailsImpl userDetails;
    private List<UserFriendResponseDTO> friendsList;
    private List<TransactionResponseDTO> transactionsList;

    @BeforeEach
    public void setup() {
        // Création d'un utilisateur de test
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setBalance(100.0);
        testUser.setFriends(new ArrayList<>());

        // Création d'un UserDetailsImpl pour simuler l'authentification
        userDetails = new UserDetailsImpl(testUser);

        // Création d'une liste d'amis pour les tests
        friendsList = new ArrayList<>();
        friendsList.add(new UserFriendResponseDTO(2L, "frienduser", "friend@example.com"));

        // Création d'une liste de transactions pour les tests
        transactionsList = new ArrayList<>();
        TransactionResponseDTO transaction = new TransactionResponseDTO();
        transaction.setId(1L);
        transaction.setAmount(50.0);
        transaction.setDescription("Test transaction");
        transaction.setReceiverUsername("frienduser");
        transactionsList.add(transaction);

        // Configuration des mocks
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.getFriends(1L)).thenReturn(friendsList);
        when(transactionService.getFilteredTransactionsByUser(testUser)).thenReturn(transactionsList);
        doNothing().when(transactionService).addNewTransaction(anyLong(), anyString(), anyString(), anyDouble());
        doNothing().when(userService).addBalance(any(User.class), anyDouble());
    }

    @Test
    @DisplayName("Devrait afficher la page de transaction")
    @WithMockUser
    public void testTransactionHome() throws Exception {
        mockMvc.perform(get("/transaction")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("friendsList"))
                .andExpect(model().attributeExists("transactionsList"))
                .andExpect(model().attributeExists("userBalance"))
                .andExpect(model().attribute("friendsList", friendsList))
                .andExpect(model().attribute("transactionsList", transactionsList))
                .andExpect(model().attribute("userBalance", 100.0));

        verify(userService, times(1)).getFriends(1L);
        verify(userService, times(2)).getUserById(1L);
        verify(transactionService, times(1)).getFilteredTransactionsByUser(testUser);
    }

    @Test
    @DisplayName("Devrait effectuer un paiement avec succès")
    @WithMockUser
    public void testPay() throws Exception {
        mockMvc.perform(post("/transaction/pay")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("email", "friend@example.com")
                .param("description", "Test payment")
                .param("amount", "50.0"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"))
                .andExpect(flash().attributeExists("success"));

        verify(transactionService, times(1)).addNewTransaction(eq(1L), eq("friend@example.com"), eq("Test payment"),
                eq(50.0));
    }

    @Test
    @DisplayName("Devrait effectuer un dépôt avec succès")
    @WithMockUser
    public void testDeposit() throws Exception {
        mockMvc.perform(post("/transaction/deposit")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("amount", "100.0"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"))
                .andExpect(flash().attributeExists("success"));

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).addBalance(eq(testUser), eq(100.0));
    }
}