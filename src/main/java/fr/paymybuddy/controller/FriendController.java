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

/**
 * Controller responsible for managing friend relationships between users.
 * <p>
 * This controller handles all operations related to the user's friends,
 * including viewing, adding, and removing friends from a user's contact list.
 * All endpoints in this controller are mapped under the "/friends" path.
 * </p>
 * 
 * @author Pay My Buddy
 * @version 1.0
 */
@Controller
@RequestMapping("/friends")
public class FriendController {

    /** Service handling user-related operations */
    private UserService userService;

    /**
     * Default constructor.
     */
    public FriendController() {
    }

    /**
     * Constructs a new FriendController with the specified UserService.
     * 
     * @param userService the service for user-related operations
     */
    @Autowired
    public FriendController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the list of friends for the authenticated user.
     * <p>
     * This endpoint retrieves all friends associated with the currently
     * authenticated user and displays them on the friends page.
     * </p>
     * 
     * @param model       the Spring MVC model for passing data to the view
     * @param userDetails the details of the authenticated user
     * @return the name of the view to render
     */
    @GetMapping
    public String showFriends(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserFriendResponseDTO> friends = userService.getFriends(userDetails.getId());
        model.addAttribute("friends", friends);
        return "friends";
    }

    /**
     * Adds a new friend to the authenticated user's list.
     * <p>
     * This endpoint adds a new friend relationship between the authenticated user
     * and another user identified by their email address.
     * </p>
     * 
     * @param userDetails        the details of the authenticated user
     * @param email              the email address of the user to add as a friend
     * @param redirectAttributes for passing flash attributes to the redirected URL
     * @return a redirect URL to the friends page with an "add" parameter
     */
    @GetMapping("/add")
    public String addFriend(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        User friend = userService.getUserByEmail(email);
        userService.addFriend(userDetails.getId(), friend.getId());
        redirectAttributes.addFlashAttribute("success", "Friend added successfully");

        return "redirect:/friends?add";
    }

    /**
     * Removes a friend from the authenticated user's list.
     * <p>
     * This endpoint removes an existing friend relationship between the
     * authenticated
     * user and the specified friend identified by their email address.
     * </p>
     * 
     * @param userDetails        the details of the authenticated user
     * @param email              the email address of the friend to be removed
     * @param redirectAttributes for passing flash attributes to the redirected URL
     * @return a redirect URL to the friends page with a "del" parameter
     */
    @GetMapping("/del")
    public String delFriend(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        User friend = userService.getUserByEmail(email);
        userService.deleteFriend(userDetails.getId(), friend.getId());
        redirectAttributes.addFlashAttribute("success", "Friend deleted successfully");

        return "redirect:/friends?del";
    }
}