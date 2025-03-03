package fr.paymybuddy.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("Un(e) %s avec %s '%s' existe déjà", resourceName, fieldName, fieldValue));
    }
}