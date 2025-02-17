package fr.paymybuddy.controller;

import fr.paymybuddy.model.User;
import fr.paymybuddy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Ajouter un utilisateur via POST
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.addUser(user));
    }

    // Récupérer tous les utilisateurs via GET
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
