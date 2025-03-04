package fr.paymybuddy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import fr.paymybuddy.config.TestSecurityConfig;
import fr.paymybuddy.dto.UserFormRequestDTO;
import fr.paymybuddy.mapper.UserMapper;
import fr.paymybuddy.model.User;
import fr.paymybuddy.service.UserService;

@WebMvcTest(SignupController.class)
@Import(TestSecurityConfig.class)
public class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    private UserFormRequestDTO testUserDTO;
    private User testUser;

    @BeforeEach
    public void setup() {
        // Création d'un utilisateur DTO de test
        testUserDTO = new UserFormRequestDTO();
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setUsername("testuser");
        testUserDTO.setPassword("password123");

        // Création d'un utilisateur de test
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("password123");

        // Configuration des mocks
        when(userMapper.toUser(any(UserFormRequestDTO.class))).thenReturn(testUser);
    }

    @Test
    @WithMockUser
    @DisplayName("Devrait afficher la page d'inscription")
    public void testGetSignupPage() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    @DisplayName("Devrait traiter une inscription valide et rediriger vers la page de connexion")
    public void testValidSignup() throws Exception {
        mockMvc.perform(post("/signup-processing")
                .with(csrf())
                .param("email", "test@example.com")
                .param("username", "testuser")
                .param("password", "password123"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userMapper, times(1)).toUser(any(UserFormRequestDTO.class));
        verify(userService, times(1)).saveNewUser(any(User.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Devrait retourner à la page d'inscription en cas d'erreurs de validation")
    public void testInvalidSignup() throws Exception {
        mockMvc.perform(post("/signup-processing")
                .with(csrf())
                .param("email", "") // Email vide pour déclencher une erreur de validation
                .param("username", "testuser")
                .param("password", "password123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));

        // Aucun appel au service ne devrait être effectué
        verify(userService, times(0)).saveNewUser(any(User.class));
    }
}