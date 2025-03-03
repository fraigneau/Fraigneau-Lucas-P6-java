package fr.paymybuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.UserFriendResponseDTO;
import fr.paymybuddy.model.User;
import fr.paymybuddy.service.UserService;

@Controller
@RequestMapping("/friends")
public class FriendController {

    private UserService userService;

    public FriendController() {
    }

    @Autowired
    public FriendController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showFriends(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserFriendResponseDTO> friends = userService.getFriends(userDetails.getId());
        model.addAttribute("friends", friends);
        return "friends";
    }

    @GetMapping("/add")
    public String addFriend(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        User friend = userService.getUserByEmail(email);
        userService.addFriend(userDetails.getId(), friend.getId());
        redirectAttributes.addFlashAttribute("success", "Friend added successfully");

        return "redirect:/friends?add";
    }

    @GetMapping("/del")
    public String delFriend(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        User friend = userService.getUserByEmail(email);
        userService.deleteFriend(userDetails.getId(), friend.getId());
        redirectAttributes.addFlashAttribute("success", "Friend deleted successfully");

        return "redirect:/friends?del";
    }
}
