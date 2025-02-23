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

import fr.paymybuddy.dto.UserUpdateDTO;
import fr.paymybuddy.mapper.UserUpdateMapper;
import fr.paymybuddy.service.UserService;
import jakarta.validation.Valid;

@Controller
public class SignupController {

    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    private UserService userService;
    private UserUpdateMapper UserUpdateMapper;

    public SignupController() {
    }

    @Autowired
    public SignupController(UserService userService, UserUpdateMapper UserUpdateMapper) {
        this.userService = userService;
        this.UserUpdateMapper = UserUpdateMapper;
    }

    @GetMapping("/signup")
    public String login(Model model) {
        model.addAttribute("user", new UserUpdateDTO());
        logger.debug("Acces a la page d'inscription");
        return "signup";
    }

    @PostMapping("/signup-processing")
    public String signup(@Valid @ModelAttribute("user") UserUpdateDTO newuser, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Erreur lors de l'inscription: " + result.getAllErrors());
            return "signup";
        }

        logger.debug("Utilisateur inscrit avec succes: " + newuser);
        userService.saveUser(UserUpdateMapper.toUser(newuser));

        return "redirect:/login";
    }

}
