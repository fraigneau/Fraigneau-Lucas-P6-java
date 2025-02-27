package fr.paymybuddy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.mapper.TransactionMapper;
import fr.paymybuddy.service.TransactionService;
import fr.paymybuddy.service.UserService;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    private UserService userService;
    private TransactionService transactionService;
    private Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController() {
    }

    @Autowired
    public TransactionController(UserService userService, TransactionService transactionService,
            TransactionMapper transactionMapper) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping()
    public String transactionHome(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        model.addAttribute("friends", userService.getFriends(userDetails.getId()));
        model.addAttribute("balance", userService.getUserById(userDetails
                .getId()).getBalance());

        model.addAttribute("transactions",
                transactionService.getFilteredTransactionsByUser(userService.getUserById(userDetails.getId())));
        return "transaction";
    }

    @GetMapping("/pay")
    public String pay(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("receiverEmail") String receiverEmail,
            @RequestParam("description") String description,
            @RequestParam("amount") double amount) {

        try {
            transactionService.addNewTransaction(userDetails.getId(), receiverEmail, description, amount);
        } catch (IllegalArgumentException balence) {
            logger.error("Transaction failed {}", balence.getMessage());
            return "redirect:/transaction?verror";
        }
        return "redirect:/transaction?vsuceess";
    }

    @GetMapping("/deposit")
    public String deposit(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("count") double count) {

        try {
            userService.addBalance(userService.getUserById(userDetails.getId()), count);
        } catch (IllegalArgumentException balence) {
            logger.error("Transaction failed {}", balence.getMessage());
            return "redirect:/transaction?ferror";
        }

        return "redirect:/transaction?fsucess";
    }

}