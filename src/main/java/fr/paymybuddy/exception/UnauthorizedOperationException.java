package fr.paymybuddy.exception;

public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }

    public UnauthorizedOperationException(String operation, String resourceName, Long resourceId) {
        super(String.format("Vous n'êtes pas autorisé à %s %s avec ID: %d", operation, resourceName, resourceId));
    }
}