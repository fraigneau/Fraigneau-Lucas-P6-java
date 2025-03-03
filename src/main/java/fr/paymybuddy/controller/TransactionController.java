package fr.paymybuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.dto.BalanceRequestDTO;
import fr.paymybuddy.dto.TransactionRequestDTO;
import fr.paymybuddy.mapper.TransactionMapper;
import fr.paymybuddy.service.TransactionService;
import fr.paymybuddy.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    private UserService userService;
    private TransactionService transactionService;

    public TransactionController() {
    }

    @Autowired
    public TransactionController(UserService userService, TransactionService transactionService,
            TransactionMapper transactionMapper) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping()
    public String transactionHome(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails,
            BalanceRequestDTO balanceRequest, TransactionRequestDTO transactionRequest) {

        model.addAttribute("balanceResponse", balanceRequest);
        model.addAttribute("transactionResponse", transactionRequest);

        model.addAttribute("friendsList", userService.getFriends(userDetails
                .getId()));

        model.addAttribute("transactionsList", transactionService.getFilteredTransactionsByUser(userService
                .getUserById(userDetails.getId())));

        model.addAttribute("userBalance", userService.getUserById(userDetails
                .getId()).getBalance());

        return "transaction";
    }

    @PostMapping("/pay")
    public String pay(@ModelAttribute @Valid TransactionRequestDTO transactionRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes) {

        transactionService.addNewTransaction(userDetails.getId(), transactionRequest.getEmail(),
                transactionRequest.getDescription(), transactionRequest.getAmount());

        redirectAttributes.addFlashAttribute("success", "Payment successful");

        return "redirect:/transaction";
    }

    @PostMapping("/deposit")
    public String deposit(@ModelAttribute @Valid BalanceRequestDTO balanceRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes) {

        userService.addBalance(userService.getUserById(userDetails.getId()), balanceRequest.getAmount());
        redirectAttributes.addFlashAttribute("success", "Deposit successful");
        return "redirect:/transaction";
    }

}