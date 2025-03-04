package fr.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling authentication-related requests.
 * <p>
 * This controller manages endpoints related to user authentication,
 * such as login. It maps HTTP requests to the appropriate view templates.
 *
 * @author PayMyBuddy
 * @version 1.0
 * @see Controller
 */
@Controller
public class AuthController {

    /**
     * Handles GET requests to the login page.
     * <p>
     * This endpoint displays the login form to the user.
     *
     * @return the name of the login view template
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

}