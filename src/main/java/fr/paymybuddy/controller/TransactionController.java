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

/**
 * Controller responsible for handling financial transactions functionality.
 * <p>
 * This controller manages all transaction-related operations, including:
 * <ul>
 * <li>Displaying transaction history</li>
 * <li>Processing payments between users</li>
 * <li>Handling balance deposits</li>
 * </ul>
 * </p>
 *
 * @author PayMyBuddy
 * @version 1.0
 */
@Controller
@RequestMapping("/transaction")
public class TransactionController {

        private UserService userService;
        private TransactionService transactionService;

        /**
         * Default constructor for TransactionController.
         */
        public TransactionController() {
        }

        /**
         * Constructor with dependency injection for TransactionController.
         *
         * @param userService        The service handling user-related operations
         * @param transactionService The service handling transaction-related operations
         * @param transactionMapper  The mapper for converting between Transaction and
         *                           DTO objects
         */
        @Autowired
        public TransactionController(UserService userService, TransactionService transactionService,
                        TransactionMapper transactionMapper) {
                this.userService = userService;
                this.transactionService = transactionService;
        }

        /**
         * Displays the transaction homepage with user's financial information.
         * <p>
         * This endpoint renders the transaction page with:
         * <ul>
         * <li>User's current balance</li>
         * <li>List of friends for making payments</li>
         * <li>Transaction history</li>
         * <li>Forms for making payments and deposits</li>
         * </ul>
         * 
         *
         * @param model              The Spring MVC model to add attributes to
         * @param userDetails        The authenticated user's details
         * @param balanceRequest     The DTO for handling balance operations
         * @param transactionRequest The DTO for handling transaction operations
         * @return The name of the view template to render
         */
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

        /**
         * Processes a payment transaction between users.
         * <p>
         * This endpoint handles the form submission for making a payment to another
         * user.
         * It creates a new transaction and redirects back to the transaction page with
         * a success message.
         *
         *
         * @param transactionRequest The DTO containing payment details from the form
         * @param userDetails        The authenticated user's details
         * @param redirectAttributes The redirect attributes for flash messages
         * @return A redirect URL to the transaction page
         */
        @PostMapping("/pay")
        public String pay(@ModelAttribute TransactionRequestDTO transactionRequest,
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        RedirectAttributes redirectAttributes) {

                transactionService.addNewTransaction(userDetails.getId(), transactionRequest.getEmail(),
                                transactionRequest.getDescription(), transactionRequest.getAmount());

                redirectAttributes.addFlashAttribute("success", "Payment successful");

                return "redirect:/transaction";
        }

        /**
         * Processes a balance deposit transaction.
         * <p>
         * This endpoint handles the form submission for adding funds to the user's
         * account.
         * It updates the user's balance and redirects back to the transaction page with
         * a success message.
         *
         *
         * @param balanceRequest     The DTO containing deposit amount from the form
         * @param userDetails        The authenticated user's details
         * @param redirectAttributes The redirect attributes for flash messages
         * @return A redirect URL to the transaction page
         */
        @PostMapping("/deposit")
        public String deposit(@ModelAttribute BalanceRequestDTO balanceRequest,
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        RedirectAttributes redirectAttributes) {

                userService.addBalance(userService.getUserById(userDetails.getId()), balanceRequest.getAmount());
                redirectAttributes.addFlashAttribute("success", "Deposit successful");
                return "redirect:/transaction";
        }
}