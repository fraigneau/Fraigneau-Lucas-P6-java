package fr.paymybuddy.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalanceException(InsufficientBalanceException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/transaction";
    }

    @ExceptionHandler(SelfSendingAmountException.class)
    public String handleSelfSendingAmountException(SelfSendingAmountException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/transaction";
    }

    @ExceptionHandler(ContactAlreadyExistException.class)
    public String handleContactAlreadyExistException(ContactAlreadyExistException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/friends";
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResourceException(DuplicateResourceException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/friends";
    }

    @ExceptionHandler(UserNotFondExeption.class)
    public String handleUserNotFondExeption(UserNotFondExeption ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/friends";
    }

}