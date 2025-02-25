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
import fr.paymybuddy.dto.UserFormDTO;
import fr.paymybuddy.mapper.UserMapper;
import fr.paymybuddy.service.UserService;
import jakarta.validation.Valid;

@Controller
public class ProfilController {

    private static final Logger logger = LoggerFactory.getLogger(ProfilController.class);

    private UserService userService;
    private UserMapper userMapper;

    public ProfilController() {
    }

    @Autowired
    public ProfilController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/profil")
    public String profil(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            UserFormDTO actualUserForm = userMapper.toUserFormDTO(userService.getUserById(userDetails.getId()));
            model.addAttribute("user", actualUserForm);
        } else {
            logger.warn("Aucun utilisateur authentifié !");
            return "redirect:/login";
        }
        return "profil";
    }

    @PostMapping("/profil-processing")
    public String profilProssesing(@Valid @ModelAttribute("user") UserFormDTO updatedUser,
            BindingResult result, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (result.hasErrors()) {
            return "profil";
        }
        userService.updateUser(updatedUser, userDetails.getId());
        return "profil";
    }

}
