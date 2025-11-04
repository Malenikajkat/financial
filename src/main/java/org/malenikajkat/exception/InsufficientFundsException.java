package org.malenikajkat.exception;

public class InsufficientFundsException extends Exception {

    private final double requiredAmount;
    private final double availableBalance;

    public InsufficientFundsException(double requiredAmount, double availableBalance) {
        super(String.format("Требуемая сумма %.2f превышает доступный баланс %.2f", requiredAmount, availableBalance));
        this.requiredAmount = requiredAmount;
        this.availableBalance = availableBalance;
    }

    public double getRequiredAmount() {
        return requiredAmount;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }
}