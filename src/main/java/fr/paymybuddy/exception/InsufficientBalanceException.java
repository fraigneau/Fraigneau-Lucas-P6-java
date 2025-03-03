package fr.paymybuddy.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(double currentBalance, double requiredAmount) {
        super(String.format("Solde insuffisant. Solde actuel: %.2f€, Montant requis: %.2f€", currentBalance,
                requiredAmount));
    }
}