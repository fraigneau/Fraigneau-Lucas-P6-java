package fr.paymybuddy.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import fr.paymybuddy.dto.UserFriendResponseDTO;
import fr.paymybuddy.model.User;
import fr.paymybuddy.service.UserService;

@WebMvcTest(FriendController.class)
@Import(TestSecurityConfig.class)
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private User testUser;
    private User friendUser;
    private UserDetailsImpl userDetails;
    private List<UserFriendResponseDTO> friendsList;

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

        // Création d'un ami de test
        friendUser = new User();
        friendUser.setId(2L);
        friendUser.setEmail("friend@example.com");
        friendUser.setUsername("frienduser");
        friendUser.setPassword("password");
        friendUser.setBalance(200.0);
        friendUser.setFriends(new ArrayList<>());

        // Création d'un UserDetailsImpl pour simuler l'authentification
        userDetails = new UserDetailsImpl(testUser);

        // Création d'une liste d'amis pour les tests
        friendsList = new ArrayList<>();
        friendsList.add(new UserFriendResponseDTO(2L, "frienduser", "friend@example.com"));

        // Configuration des mocks
        when(userService.getFriends(1L)).thenReturn(friendsList);
        when(userService.getUserByEmail("friend@example.com")).thenReturn(friendUser);
    }

    @Test
    @DisplayName("Devrait afficher la liste des amis")
    @WithMockUser
    public void testShowFriends() throws Exception {
        mockMvc.perform(get("/friends")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("friends"))
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attribute("friends", friendsList));

        verify(userService, times(1)).getFriends(1L);
    }

    @Test
    @DisplayName("Devrait ajouter un ami avec succès")
    @WithMockUser
    public void testAddFriend() throws Exception {
        mockMvc.perform(get("/friends/add")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .param("email", "friend@example.com"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends?add"))
                .andExpect(flash().attributeExists("success"));

        verify(userService, times(1)).getUserByEmail("friend@example.com");
        verify(userService, times(1)).addFriend(eq(1L), eq(2L));
    }

    @Test
    @DisplayName("Devrait supprimer un ami avec succès")
    @WithMockUser
    public void testDeleteFriend() throws Exception {
        mockMvc.perform(get("/friends/del")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .param("email", "friend@example.com"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends?del"))
                .andExpect(flash().attributeExists("success"));

        verify(userService, times(1)).getUserByEmail("friend@example.com");
        verify(userService, times(1)).deleteFriend(eq(1L), eq(2L));
    }
}