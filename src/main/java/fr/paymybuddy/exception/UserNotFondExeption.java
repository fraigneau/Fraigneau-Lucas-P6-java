package fr.paymybuddy.exception;

/**
 * Exception lancée lorsqu'un utilisateur demandé n'est pas trouvé dans le
 * système.
 * Cette exception est utilisée spécifiquement pour gérer les cas où un
 * utilisateur
 * recherché n'existe pas dans la base de données.
 */
public class UserNotFondExeption extends RuntimeException {

    /**
     * Construit une nouvelle exception UserNotFondExeption avec le message d'erreur
     * spécifié.
     *
     * @param message le message détaillant la raison de l'exception
     */
    public UserNotFondExeption(String message) {
        super(message);
    }
}