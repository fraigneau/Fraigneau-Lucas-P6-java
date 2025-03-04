package fr.paymybuddy.exception;

/**
 * Exception lancée lorsqu'une ressource demandée n'est pas trouvée dans le
 * système.
 * Cette exception est utilisée pour gérer les cas où une entité recherchée
 * (utilisateur, transaction, etc.) n'existe pas dans la base de données.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construit une nouvelle exception ResourceNotFoundException avec le message
     * d'erreur spécifié.
     *
     * @param message le message détaillant la raison de l'exception
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}