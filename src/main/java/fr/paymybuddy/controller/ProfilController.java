package fr.paymybuddy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.LoginResponseDTO;

@Controller
public class ProfilController {

    private static final Logger logger = LoggerFactory.getLogger(ProfilController.class);
    private LoginResponseDTO loginResponseDTO;

    @GetMapping("/profil")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            loginResponseDTO = new LoginResponseDTO(userDetails);
            model.addAttribute("user", loginResponseDTO);
        } else { // TODO exception Usernotfound
            logger.warn("Aucun utilisateur authentifie !");
        }
        return "profil";
    }

}
