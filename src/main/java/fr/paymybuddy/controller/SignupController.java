package fr.paymybuddy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import fr.paymybuddy.dto.UserFormRequestDTO;
import fr.paymybuddy.mapper.UserMapper;
import fr.paymybuddy.service.UserService;
import jakarta.validation.Valid;

/**
 * Controller responsible for handling user registration functionality.
 * <p>
 * This controller manages the user signup process, including displaying
 * the registration form and processing user registration submissions.
 * </p>
 *
 * @author PayMyBuddy
 * @version 1.0
 */
@Controller
public class SignupController {

    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    private UserService userService;
    private UserMapper userUpdateMapper;

    /**
     * Default constructor for SignupController.
     */
    public SignupController() {
    }

    /**
     * Constructor with dependency injection for SignupController.
     *
     * @param userService      The service handling user-related operations
     * @param userUpdateMapper The mapper for converting between User and DTO
     *                         objects
     */
    @Autowired
    public SignupController(UserService userService, UserMapper userUpdateMapper) {
        this.userService = userService;
        this.userUpdateMapper = userUpdateMapper;
    }

    /**
     * Displays the user registration page.
     * <p>
     * This endpoint renders the signup form and adds an empty user DTO
     * to the model for form binding.
     * </p>
     *
     * @param model The Spring MVC model to add attributes to
     * @return The name of the view template to render
     */
    @GetMapping("/signup")
    public String login(Model model) {
        model.addAttribute("user", new UserFormRequestDTO());
        logger.debug("Accessing registration page");
        return "signup";
    }

    /**
     * Processes the user registration submission.
     * <p>
     * This endpoint handles the form submission for user registration,
     * validates the input data, and creates a new user account if all
     * validations pass.
     * </p>
     *
     * @param newUser The DTO containing user registration data from the form
     * @param result  The binding result containing validation errors, if any
     * @param model   The Spring MVC model to add attributes to
     * @return The name of the view template to render or a redirect URL
     */
    @PostMapping("/signup-processing")
    public String signup(@Valid @ModelAttribute("user") UserFormRequestDTO newUser,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            logger.error("Error during registration: " + result.getAllErrors());
            return "signup";
        }

        logger.debug("User registered successfully: " + newUser);
        userService.saveNewUser(userUpdateMapper.toUser(newUser));

        return "redirect:/login";
    }
}