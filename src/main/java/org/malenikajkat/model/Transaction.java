package org.malenikajkat.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Locale;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double amount;
    private final String description;
    private final String category;
    private final Type type;
    private final LocalDate date;

    public Transaction(double amount, String description, String category, Type type, LocalDate date) {
        validateAmount(amount);
        validateDescription(description);
        validateCategory(category);
        validateType(type);

        this.amount = amount;
        this.description = description.trim();
        this.category = category.trim();
        this.type = type;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Type getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isIncome() {
        return type == Type.INCOME;
    }

    public String getTypeLabel() {
        return isIncome() ? "Доход" : "Расход";
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.2f руб. (%s: %s)", amount, getTypeLabel(), category);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Transaction)) return false;
        Transaction that = (Transaction) obj;
        return Double.compare(this.amount, that.amount) == 0 &&
                this.isIncome() == that.isIncome() &&
                Objects.equals(this.category, that.category) &&
                Objects.equals(this.date, that.date) &&
                Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, description, category, isIncome(), date);
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма транзакции должна быть положительной. Получено: " + amount);
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Описание транзакции не может быть null или пустым");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория транзакции не может быть null или пустой");
        }
    }

    private void validateType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип транзакции (Type) не может быть null");
        }
    }

    public enum Type {
        INCOME,
        EXPENSE
    }
}