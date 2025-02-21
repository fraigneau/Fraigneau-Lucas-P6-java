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

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.UserUpdateDTO;
import fr.paymybuddy.service.UserService;
import jakarta.validation.Valid;

@Controller
public class ProfilController {

    private static final Logger logger = LoggerFactory.getLogger(ProfilController.class);

    private UserService userService;

    public ProfilController() {
    }

    @Autowired
    public ProfilController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profil")
    public String profil(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            UserUpdateDTO user = new UserUpdateDTO(userDetails.getUsername(),
                    userDetails.getEmail(), null);
            model.addAttribute("user", user);
        } else {
            logger.warn("Aucun utilisateur authentifié !");
            return "redirect:/login";
        }
        return "profil";
    }

    @PostMapping("/profil-processing")
    public String profilProssesing(@Valid @ModelAttribute("user") UserUpdateDTO updatedUser,
            BindingResult result, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (result.hasErrors()) {
            return "profil";
        }
        userService.updateUser(updatedUser, userDetails.getId());
        return "profil";
    }

}
