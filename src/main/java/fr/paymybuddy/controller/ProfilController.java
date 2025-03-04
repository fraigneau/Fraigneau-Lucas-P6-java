package fr.paymybuddy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.UserFormRequestDTO;
import fr.paymybuddy.mapper.UserMapper;
import fr.paymybuddy.service.UserService;
import jakarta.validation.Valid;

/**
 * Controller responsible for managing user profile operations.
 * <p>
 * This controller handles all operations related to viewing and updating
 * a user's profile information. It provides endpoints for displaying the
 * profile page and processing profile updates.
 * </p>
 * 
 * @author Pay My Buddy
 * @version 1.0
 */
@Controller
public class ProfilController {

    /** Logger for this class */
    private static final Logger logger = LoggerFactory.getLogger(ProfilController.class);

    /** Service handling user-related operations */
    private UserService userService;

    /** Mapper for converting between User entities and DTOs */
    private UserMapper userMapper;

    /**
     * Default constructor.
     */
    public ProfilController() {
    }

    /**
     * Constructs a new ProfilController with the specified dependencies.
     * 
     * @param userService the service for user-related operations
     * @param userMapper  the mapper for converting between User entities and DTOs
     */
    @Autowired
    public ProfilController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Displays the user profile page.
     * <p>
     * This endpoint retrieves the authenticated user's profile information
     * and displays it on the profile page. If no user is authenticated,
     * the user is redirected to the login page.
     * </p>
     * 
     * @param model       the Spring MVC model for passing data to the view
     * @param userDetails the details of the authenticated user
     * @return the name of the view to render or a redirect to the login page if no
     *         user is authenticated
     */
    @GetMapping("/profil")
    public String profil(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            UserFormRequestDTO actualUserForm = userMapper.toUserFormDTO(userService.getUserById(userDetails.getId()));
            model.addAttribute("user", actualUserForm);
        } else {
            logger.warn("Aucun utilisateur authentifié !");
            return "redirect:/login";
        }
        return "profil";
    }

    /**
     * Processes a user profile update.
     * <p>
     * This endpoint validates and processes a user profile update request.
     * If validation errors occur, the user is returned to the profile page with
     * error messages.
     * Upon successful update, a success message is displayed and the user is
     * redirected
     * back to the profile page.
     * </p>
     * 
     * @param updatedUser        the updated user information from the form
     * @param result             the binding result containing validation errors, if
     *                           any
     * @param userDetails        the details of the authenticated user
     * @param redirectAttributes for passing flash attributes to the redirected URL
     * @return the name of the view to render or a redirect to the profile page on
     *         success
     */
    @PostMapping("/profil-processing")
    public String profilProssesing(@Valid @ModelAttribute("user") UserFormRequestDTO updatedUser,
            BindingResult result, @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profil";
        }

        userService.updateUser(updatedUser, userDetails.getId());
        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");

        return "redirect:/profil";
    }
}