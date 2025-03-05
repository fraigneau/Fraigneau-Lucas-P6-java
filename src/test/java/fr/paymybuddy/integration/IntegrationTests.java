package fr.paymybuddy.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.UserFriendResponseDTO;
import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.UserRepository;
import fr.paymybuddy.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Tag("integration")
@Transactional
public class IntegrationTests {

    private static final String TEST_USER_EMAIL = "test@example.com";
    private static final String TEST_USER_PASSWORD = "password123";
    private static final String TEST_USER_USERNAME = "testuser";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User testUser;
    private UserDetailsImpl userDetails;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create a test user if it doesn't exist
        Optional<User> existingUser = userRepository.findByEmail(TEST_USER_EMAIL);
        if (existingUser.isPresent()) {
            testUser = existingUser.get();
        } else {
            // Create and save a test user
            testUser = new User();
            testUser.setUsername(TEST_USER_USERNAME);
            testUser.setEmail(TEST_USER_EMAIL);
            testUser.setPassword(TEST_USER_PASSWORD);
            testUser.setBalance(100.0);
            testUser.setFriends(new ArrayList<>());
            testUser = userRepository.save(testUser);
        }

        userDetails = new UserDetailsImpl(testUser);
    }

    /*---------- Authentication Tests ----------*/

    @Test
    public void homePageRedirectsToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void loginPageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void loginRedirectsToProfile_whenCredentialsAreValid() throws Exception {
        // Create a user with known credentials in the test database
        String loginEmail = "login@example.com";
        Optional<User> existingLoginUser = userRepository.findByEmail(loginEmail);

        if (existingLoginUser.isEmpty()) {
            User loginUser = new User();
            loginUser.setUsername("loginuser");
            loginUser.setEmail(loginEmail);
            // Note: In a real test, you would use your password encoder here
            // For simplicity in this example, we're using a plain password
            loginUser.setPassword("$2a$10$rB1d.Kpje/Oj6Bn/ZBUDfO7xux6hN5g.eXMWoc37s2isvg6Z1Bnq2"); // encoded "123123"
            userRepository.save(loginUser);
        }

        mockMvc.perform(post("/login")
                .param("username", loginEmail)
                .param("password", "123123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));
    }

    @Test
    public void loginFailsAndRedirects_whenCredentialsAreInvalid() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "nonexistent@example.com")
                .param("password", "wrongpassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    /*---------- Registration Tests ----------*/

    @Test
    public void signupPageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @Transactional
    public void userRegistrationSucceeds_withValidData() throws Exception {
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "@example.com";

        mockMvc.perform(post("/signup-processing")
                .with(csrf())
                .param("username", "newuser")
                .param("email", uniqueEmail)
                .param("password", "password123"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Verify user was created in the database
        Optional<User> createdUser = userRepository.findByEmail(uniqueEmail);
        assertTrue(createdUser.isPresent(), "User should have been created");
        assertEquals("newuser", createdUser.get().getUsername());
    }

    @Test
    public void userRegistrationFails_withInvalidData() throws Exception {
        mockMvc.perform(post("/signup-processing")
                .with(csrf())
                .param("username", "u") // Too short
                .param("email", "invalid-email") // Invalid email
                .param("password", "pwd")) // Too short
                .andDo(print())
                .andExpect(status().isOk()) // Stays on the same page
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors());
    }

    /*---------- Profile Tests ----------*/

    @Test
    @WithMockUser
    public void profilePageLoads_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/profil")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @Transactional
    public void profileUpdateSucceeds_withValidData() throws Exception {
        // Create a user with known credentials in the test database
        String profileEmail = "profileupdate@example.com";
        User profileUser;
        Optional<User> existingProfileUser = userRepository.findByEmail(profileEmail);

        if (existingProfileUser.isPresent()) {
            profileUser = existingProfileUser.get();
        } else {
            profileUser = new User();
            profileUser.setUsername("oldUsername");
            profileUser.setEmail(profileEmail);
            profileUser.setPassword("$2a$10$rB1d.Kpje/Oj6Bn/ZBUDfO7xux6hN5g.eXMWoc37s2isvg6Z1Bnq2"); // encoded "123123"
            profileUser = userRepository.save(profileUser);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(profileUser);

        mockMvc.perform(post("/profil-processing")
                .with(csrf())
                .with(user(userDetails))
                .param("username", "newUsername")
                .param("email", profileEmail)
                .param("password", ""))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attributeExists("success"));

        // Verify profile was updated
        User updatedUser = userRepository.findByEmail(profileEmail).orElseThrow();
        assertEquals("newUsername", updatedUser.getUsername());
    }

    /*---------- Friends Management Tests ----------*/

    @Test
    @WithMockUser
    public void friendsPageLoads_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/friends")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("friends"))
                .andExpect(model().attributeExists("friends"));
    }

    @Test
    @Transactional
    public void addFriendSucceeds_withValidEmail() throws Exception {
        // Create two users
        String user1Email = "user1@example.com";
        String user2Email = "user2@example.com";
        User user1, user2;

        // Create or retrieve first user
        Optional<User> existingUser1 = userRepository.findByEmail(user1Email);
        if (existingUser1.isPresent()) {
            user1 = existingUser1.get();
        } else {
            user1 = new User();
            user1.setUsername("user1");
            user1.setEmail(user1Email);
            user1.setPassword("password");
            user1.setFriends(new ArrayList<>());
            user1 = userRepository.save(user1);
        }

        // Create or retrieve second user
        Optional<User> existingUser2 = userRepository.findByEmail(user2Email);
        if (existingUser2.isPresent()) {
            user2 = existingUser2.get();
        } else {
            user2 = new User();
            user2.setUsername("user2");
            user2.setEmail(user2Email);
            user2.setPassword("password");
            user2.setFriends(new ArrayList<>());
            user2 = userRepository.save(user2);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user1);

        mockMvc.perform(get("/friends/add")
                .with(user(userDetails))
                .param("email", user2Email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends?add"))
                .andExpect(flash().attributeExists("success"));

        // Verify friendship was created
        List<UserFriendResponseDTO> friends = userService.getFriends(user1.getId());
        boolean foundFriend = friends.stream()
                .anyMatch(friend -> friend.getEmail().equals(user2Email));

        assertTrue(foundFriend, "Friend should have been added");
    }

    @Test
    @Transactional
    public void removeFriendSucceeds_withExistingConnection() throws Exception {
        // Create two users
        String user3Email = "user3@example.com";
        String user4Email = "user4@example.com";
        User user3, user4;

        // Create or retrieve first user
        Optional<User> existingUser3 = userRepository.findByEmail(user3Email);
        if (existingUser3.isPresent()) {
            user3 = existingUser3.get();
        } else {
            user3 = new User();
            user3.setUsername("user3");
            user3.setEmail(user3Email);
            user3.setPassword("password");
            user3.setFriends(new ArrayList<>());
            user3 = userRepository.save(user3);
        }

        // Create or retrieve second user
        Optional<User> existingUser4 = userRepository.findByEmail(user4Email);
        if (existingUser4.isPresent()) {
            user4 = existingUser4.get();
        } else {
            user4 = new User();
            user4.setUsername("user4");
            user4.setEmail(user4Email);
            user4.setPassword("password");
            user4.setFriends(new ArrayList<>());
            user4 = userRepository.save(user4);
        }

        // Add friend relationship
        userService.addFriend(user3.getId(), user4.getId());

        UserDetailsImpl userDetails = new UserDetailsImpl(user3);

        mockMvc.perform(get("/friends/del")
                .with(user(userDetails))
                .param("email", user4Email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends?del"))
                .andExpect(flash().attributeExists("success"));

        // Verify friendship was removed
        List<UserFriendResponseDTO> friends = userService.getFriends(user3.getId());
        boolean foundFriend = friends.stream()
                .anyMatch(friend -> friend.getEmail().equals(user4Email));

        assertFalse(foundFriend, "Friend should have been removed");
    }

    /*---------- Transaction Tests ----------*/

    @Test
    @WithMockUser
    public void transactionPageLoads_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/transaction")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("balanceResponse", "transactionResponse", "friendsList",
                        "transactionsList", "userBalance"));
    }

    @Test
    @Transactional
    public void depositSucceeds_withValidAmount() throws Exception {
        // Create a user with a known balance
        String depositEmail = "deposit@example.com";
        User depositUser;

        Optional<User> existingDepositUser = userRepository.findByEmail(depositEmail);
        if (existingDepositUser.isPresent()) {
            depositUser = existingDepositUser.get();
        } else {
            depositUser = new User();
            depositUser.setUsername("deposituser");
            depositUser.setEmail(depositEmail);
            depositUser.setPassword("password");
            depositUser.setBalance(100.0);
            depositUser = userRepository.save(depositUser);
        }

        double initialBalance = depositUser.getBalance();
        UserDetailsImpl userDetails = new UserDetailsImpl(depositUser);

        mockMvc.perform(post("/transaction/deposit")
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("amount", "50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"))
                .andExpect(flash().attributeExists("success"));

        // Verify balance was updated
        User updatedUser = userRepository.findById(depositUser.getId()).orElseThrow();
        assertEquals(initialBalance + 50.0, updatedUser.getBalance(), 0.001);
    }

    @Test
    @Transactional
    public void paymentSucceeds_betweenFriends() throws Exception {
        // Create two users
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";
        User sender, receiver;

        // Create or retrieve sender
        Optional<User> existingSender = userRepository.findByEmail(senderEmail);
        if (existingSender.isPresent()) {
            sender = existingSender.get();
        } else {
            sender = new User();
            sender.setUsername("sender");
            sender.setEmail(senderEmail);
            sender.setPassword("password");
            sender.setBalance(200.0);
            sender.setFriends(new ArrayList<>());
            sender = userRepository.save(sender);
        }

        // Create or retrieve receiver
        Optional<User> existingReceiver = userRepository.findByEmail(receiverEmail);
        if (existingReceiver.isPresent()) {
            receiver = existingReceiver.get();
        } else {
            receiver = new User();
            receiver.setUsername("receiver");
            receiver.setEmail(receiverEmail);
            receiver.setPassword("password");
            receiver.setBalance(100.0);
            receiver.setFriends(new ArrayList<>());
            receiver = userRepository.save(receiver);
        }

        double senderInitialBalance = sender.getBalance();
        double receiverInitialBalance = receiver.getBalance();

        // Add friend relationship if it doesn't exist
        try {
            userService.addFriend(sender.getId(), receiver.getId());
        } catch (Exception e) {
            // Friend relationship might already exist, ignore
        }

        UserDetailsImpl senderDetails = new UserDetailsImpl(sender);

        mockMvc.perform(post("/transaction/pay")
                .with(csrf())
                .with(user(senderDetails))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", receiverEmail)
                .param("description", "Test payment")
                .param("amount", "50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"))
                .andExpect(flash().attributeExists("success"));

        // Verify balances were updated
        User updatedSender = userRepository.findById(sender.getId()).orElseThrow();
        User updatedReceiver = userRepository.findById(receiver.getId()).orElseThrow();

        assertEquals(senderInitialBalance - 50.0, updatedSender.getBalance(), 0.001);
        assertEquals(receiverInitialBalance + 50.0, updatedReceiver.getBalance(), 0.001);
    }
}