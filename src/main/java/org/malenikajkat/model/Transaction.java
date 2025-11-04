package org.malenikajkat.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double amount;
    private final String category;
    private final boolean isIncome;

    public Transaction(double amount, String category, boolean isIncome) {
        validateAmount(amount);
        validateCategory(category);

        this.amount = amount;
        this.category = category.trim();
        this.isIncome = isIncome;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public String getType() {
        return isIncome ? "доход" : "расход";
    }

    @Override
    public String toString() {
        return String.format("%.2f руб. (%s: %s)", amount, getType(), category);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Transaction)) return false;
        Transaction that = (Transaction) obj;
        return Double.compare(this.amount, that.amount) == 0 &&
                isIncome == that.isIncome &&
                category.equals(that.category);
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(amount);
        result = 31 * result + category.hashCode();
        result = 31 * result + Boolean.hashCode(isIncome);
        return result;
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма транзакции должна быть положительной (" + amount + ")");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория транзакции не может быть пустой");
        }
    }
}
