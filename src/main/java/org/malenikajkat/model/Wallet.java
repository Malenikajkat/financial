package org.malenikajkat.model;

import java.io.Serializable;
import java.util.*;

public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;

    private double balance;
    private final List<Transaction> transactions;
    private final Set<String> categories;

    public Wallet() {
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.categories = new LinkedHashSet<>();
    }

    public void addIncome(double amount, String category) {
        validateAmount(amount, "дохода");
        validateCategory(category, "дохода");

        balance += amount;
        Transaction transaction = new Transaction(amount, category, true);
        transactions.add(transaction);
        categories.add(category);
    }

    public void addExpense(double amount, String category) {
        validateAmount(amount, "расхода");
        validateCategory(category, "расхода");

        if (balance < amount) {
            throw new IllegalStateException("Недостаточно средств на счету. Текущий баланс: " + balance + ", запрашиваемая сумма: " + amount);
        }

        balance -= amount;
        Transaction transaction = new Transaction(amount, category, false);
        transactions.add(transaction);
        categories.add(category);
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public List<Transaction> getTransactionsByType(boolean isIncome) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.isIncome() == isIncome) {
                filtered.add(t);
            }
        }
        return Collections.unmodifiableList(filtered);
    }

    public double getTotalByCategory(String category, boolean isIncome) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getCategory().equalsIgnoreCase(category) && t.isIncome() == isIncome) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public Set<String> getAllCategories() {
        return Collections.unmodifiableSet(categories);
    }

    public boolean hasCategory(String category) {
        return categories.contains(category);
    }

    public void addCategory(String category) {
        validateCategory(category, "категории");
        categories.add(category.trim());
    }

    public void removeCategory(String category) {
        if (!hasCategory(category)) {
            throw new IllegalArgumentException("Категория '" + category + "' не существует");
        }
        categories.remove(category.trim());

        transactions.removeIf(t -> t.getCategory().equalsIgnoreCase(category));
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private void validateAmount(double amount, String type) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма " + type + " должна быть положительной (" + amount + ")");
        }
    }

    private void validateCategory(String category, String fieldName) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым или null");
        }
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "balance=" + balance +
                ", transactionCount=" + transactions.size() +
                ", categories=" + categories.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wallet)) return false;
        Wallet wallet = (Wallet) o;
        return Double.compare(wallet.balance, balance) == 0 &&
                transactions.equals(wallet.transactions) &&
                categories.equals(wallet.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, transactions, categories);
    }
}
