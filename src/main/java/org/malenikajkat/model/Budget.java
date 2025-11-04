package org.malenikajkat.model;

import java.io.Serializable;

public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String category;
    private final double limit;
    private double spent;

    public Budget(String category, double limit) {
        validateCategory(category);
        validateLimit(limit);

        this.category = category.trim();
        this.limit = limit;
        this.spent = 0.0;
    }

    public String getCategory() {
        return category;
    }

    public double getLimit() {
        return limit;
    }

    public double getSpent() {
        return spent;
    }

    public double getRemaining() {
        return limit - spent;
    }

    public boolean isOverBudget() {
        return spent > limit;
    }

    public boolean isNearLimit() {
        return spent >= limit * 0.8;
    }

    public void addSpent(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма расхода должна быть положительной (" + amount + ")");
        }
        spent += amount;
    }

    public void resetSpent() {
        spent = 0.0;
    }

    @Override
    public String toString() {
        return String.format("%s: лимит %.2f, потрачено %.2f, остаток %.2f",
                category, limit, spent, getRemaining());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Budget other = (Budget) obj;
        return category.equals(other.category);
    }

    @Override
    public int hashCode() {
        return category.hashCode();
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория бюджета не может быть пустой");
        }
    }

    private void validateLimit(double limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Лимит бюджета должен быть положительным (" + limit + ")");
        }
    }
}
