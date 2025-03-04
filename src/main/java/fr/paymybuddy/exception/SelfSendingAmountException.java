package fr.paymybuddy.exception;

/**
 * Exception lancée lorsqu'un utilisateur tente d'envoyer de l'argent à
 * lui-même.
 * Cette exception permet de gérer les cas où l'expéditeur et le destinataire
 * d'une transaction
 * sont la même personne, ce qui n'est pas autorisé dans l'application.
 */
public class SelfSendingAmountException extends RuntimeException {

    /**
     * Construit une nouvelle exception SelfSendingAmountException avec le message
     * d'erreur spécifié.
     *
     * @param message le message détaillant la raison de l'exception
     */
    public SelfSendingAmountException(String message) {
        super(message);
    }
}