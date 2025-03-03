package fr.paymybuddy.exception;

public class SelfSendingAmountException extends RuntimeException {
    public SelfSendingAmountException(String message) {
        super(message);
    }
}
