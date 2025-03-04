package fr.paymybuddy.exception;

/**
 * Exception lancée lorsqu'un utilisateur tente d'ajouter un contact qui existe
 * déjà.
 * Cette exception est utilisée pour gérer les tentatives de création de
 * contacts en double.
 */
public class ContactAlreadyExistException extends RuntimeException {

    /**
     * Construit une nouvelle exception ContactAlreadyExistException avec le message
     * d'erreur spécifié.
     *
     * @param message le message détaillant la raison de l'exception
     */
    public ContactAlreadyExistException(String message) {
        super(message);
    }
}