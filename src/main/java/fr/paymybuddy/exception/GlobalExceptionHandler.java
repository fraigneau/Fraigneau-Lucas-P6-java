package fr.paymybuddy.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Gestionnaire global d'exceptions pour l'application.
 * Cette classe intercepte les exceptions spécifiques lancées par l'application
 * et les transforme en réponses appropriées avec des messages d'erreur.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions de type ResourceNotFoundException.
     * Redirige vers la page de connexion avec un message d'erreur.
     *
     * @param ex                 l'exception capturée
     * @param redirectAttributes les attributs pour transmettre des données à la
     *                           page de redirection
     * @return la chaîne de redirection vers la page de connexion
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/login";
    }

    /**
     * Gère les exceptions de type InsufficientBalanceException.
     * Redirige vers la page de transaction avec un message d'erreur.
     *
     * @param ex                 l'exception capturée
     * @param redirectAttributes les attributs pour transmettre des données à la
     *                           page de redirection
     * @return la chaîne de redirection vers la page de transaction
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalanceException(InsufficientBalanceException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/transaction";
    }

    /**
     * Gère les exceptions de type SelfSendingAmountException.
     * Redirige vers la page de transaction avec un message d'erreur.
     *
     * @param ex                 l'exception capturée
     * @param redirectAttributes les attributs pour transmettre des données à la
     *                           page de redirection
     * @return la chaîne de redirection vers la page de transaction
     */
    @ExceptionHandler(SelfSendingAmountException.class)
    public String handleSelfSendingAmountException(SelfSendingAmountException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/transaction";
    }

    /**
     * Gère les exceptions de type ContactAlreadyExistException.
     * Redirige vers la page des amis avec un message d'erreur.
     *
     * @param ex                 l'exception capturée
     * @param redirectAttributes les attributs pour transmettre des données à la
     *                           page de redirection
     * @return la chaîne de redirection vers la page des amis
     */
    @ExceptionHandler(ContactAlreadyExistException.class)
    public String handleContactAlreadyExistException(ContactAlreadyExistException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/friends";
    }

    /**
     * Gère les exceptions de type DuplicateResourceException.
     * Redirige vers la page des amis avec un message d'erreur.
     *
     * @param ex                 l'exception capturée
     * @param redirectAttributes les attributs pour transmettre des données à la
     *                           page de redirection
     * @return la chaîne de redirection vers la page des amis
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResourceException(DuplicateResourceException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/friends";
    }

    /**
     * Gère les exceptions de type UserNotFondExeption.
     * Redirige vers la page des amis avec un message d'erreur.
     *
     * @param ex                 l'exception capturée
     * @param redirectAttributes les attributs pour transmettre des données à la
     *                           page de redirection
     * @return la chaîne de redirection vers la page des amis
     */
    @ExceptionHandler(UserNotFondExeption.class)
    public String handleUserNotFondExeption(UserNotFondExeption ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/friends";
    }
}