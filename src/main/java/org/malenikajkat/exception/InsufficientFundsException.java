package org.malenikajkat.exception;

public class InsufficientFundsException extends Exception {

    private final double requestedAmount;
    private final double availableAmount;

    public InsufficientFundsException(double requested, double available) {
        super(createMessage(requested, available));
        this.requestedAmount = requested;
        this.availableAmount = available;
    }

    public InsufficientFundsException(double requested, double available, Throwable cause) {
        super(createMessage(requested, available), cause);
        this.requestedAmount = requested;
        this.availableAmount = available;
    }

    private static String createMessage(double requested, double available) {
        return String.format("Недостаточно средств: запрошено %.2f, доступно %.2f", requested, available);
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }
}