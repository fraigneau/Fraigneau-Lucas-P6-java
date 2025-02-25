package fr.paymybuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.UserFriendDTO;
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
        List<UserFriendDTO> friends = userService.getFriends(userDetails.getId());
        model.addAttribute("friends", friends);
        return "friends";
    }

    @GetMapping("/add")
    public String addFriend(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("email") String email) {

        try {
            User friend = userService.getUserByEmail(email);
            userService.addFriend(userDetails.getId(), friend.getId());
        } catch (Exception e) {
            return "redirect:/friends?error";
        }
        return "redirect:/friends?add";
    }

    @GetMapping("/del")
    public String delFriend(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("email") String email) {

        try {
            User friend = userService.getUserByEmail(email);
            userService.deleteFriend(userDetails.getId(), friend.getId());

        } catch (Exception e) {
            return "redirect:/friends?error";
        }
        return "redirect:/friends?del";
    }
}
