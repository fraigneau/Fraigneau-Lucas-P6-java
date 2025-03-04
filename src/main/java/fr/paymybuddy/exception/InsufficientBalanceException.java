package fr.paymybuddy.exception;

/**
 * Exception lancée lorsqu'un utilisateur tente d'effectuer une opération
 * financière
 * avec un solde insuffisant dans son compte.
 * Cette exception est utilisée pour gérer les cas où le solde disponible ne
 * permet pas
 * de réaliser la transaction demandée.
 */
public class InsufficientBalanceException extends RuntimeException {

    /**
     * Construit une nouvelle exception InsufficientBalanceException avec le message
     * d'erreur spécifié.
     *
     * @param message le message détaillant la raison de l'exception
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}