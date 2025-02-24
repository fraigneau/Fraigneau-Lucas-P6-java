package fr.paymybuddy.controller;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.UserFriendDTO;
import fr.paymybuddy.service.UserService;

@RestController
public class RelationController {

    Logger logger = LoggerFactory.getLogger(RelationController.class);

    private UserService userService;

    public RelationController() {
    }

    @Autowired
    public RelationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/ami")
    public List<UserFriendDTO> getFriends(@AuthenticationPrincipal UserDetailsImpl actualUser) {
        logger.debug("---------- RELATION ----------");
        List<UserFriendDTO> friends = userService.getFriends(actualUser.getId());
        logger.debug("Friends List : {}", friends);
        logger.debug("---------- RELATION ----------");

        return friends;
    }

}