package fr.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RelationController {

    @GetMapping("/relation")
    public String login() {
        return "relation";
    }

}