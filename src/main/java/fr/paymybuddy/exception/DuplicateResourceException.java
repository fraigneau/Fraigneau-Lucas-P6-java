package fr.paymybuddy.exception;

/**
 * Exception lancée lorsqu'une tentative de création d'une ressource qui existe
 * déjà est effectuée.
 * Cette exception permet de gérer les cas de duplication de ressources dans
 * l'application.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Construit une nouvelle exception DuplicateResourceException avec le message
     * d'erreur spécifié.
     *
     * @param message le message détaillant la raison de l'exception
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}