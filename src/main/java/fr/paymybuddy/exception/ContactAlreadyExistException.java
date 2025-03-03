package fr.paymybuddy.exception;

public class ContactAlreadyExistException extends RuntimeException {
    public ContactAlreadyExistException(String message) {
        super(message);
    }
}