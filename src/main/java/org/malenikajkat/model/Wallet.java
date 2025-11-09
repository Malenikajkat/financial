package org.malenikajkat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;

    private double balance = 0.0;
    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Category> categories = new HashMap<>();

    @JsonProperty("totalExpense")
    private double totalExpense = 0.0;

    @JsonProperty("totalIncome")
    private double totalIncome = 0.0;

    public double getTotalExpense() { return totalExpense; }
    public void setTotalExpense(double totalExpense) { this.totalExpense = totalExpense; }

    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }


    public double getBalance() { return balance; }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Транзакция не может быть null");
        }
        transactions.add(transaction);
        updateBalance(transaction);
    }

    public List<Transaction> getTransactionsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
        return transactions.stream()
                .filter(t -> t.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<Transaction> getIncomeTransactions() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .collect(Collectors.toList());
    }

    public List<Transaction> getExpenseTransactions() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .collect(Collectors.toList());
    }

    public void clearTransactions() {
        transactions.clear();
        balance = 0.0;
        totalExpense = 0.0;
        totalIncome = 0.0;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "balance=" + balance +
                ", transactionCount=" + transactions.size() +
                "}";
    }

    private void updateBalance(Transaction transaction) {
        switch (transaction.getType()) {
            case INCOME:
                balance += transaction.getAmount();
                totalIncome += transaction.getAmount();
                break;
            case EXPENSE:
                balance -= transaction.getAmount();
                totalExpense += transaction.getAmount();
                break;
        }
    }

    public void addIncome(double amount, String category, LocalDate date) {
        validateAmount(amount);
        validateCategory(category);

        Transaction incomeTx = new Transaction(
                amount,
                "Доход",
                category,
                Transaction.Type.INCOME,
                date
        );
        addTransaction(incomeTx);
    }

    public void addExpense(double amount, String category, LocalDate date) {
        validateAmount(amount);
        validateCategory(category);

        Transaction expenseTx = new Transaction(
                amount,
                "Расход",
                category,
                Transaction.Type.EXPENSE,
                date
        );
        addTransaction(expenseTx);
    }

    public List<Transaction> getTransactionsByMonth(int year, int month) {
        return transactions.stream()
                .filter(t -> t.getDate() != null)
                .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    public double getTotalByCategoryAndMonth(String category, int year, int month, Transaction.Type type) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Тип транзакции не может быть null");
        }

        return getTransactionsByMonth(year, month).stream()
                .filter(t -> t.getCategory().equals(category) && t.getType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public List<Transaction> getTransactionsByType(Transaction.Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип транзакции не может быть null");
        }
        return transactions.stream()
                .filter(tx -> tx.getType() == type)
                .collect(Collectors.toList());
    }

    public double getTotalAmountByType(Transaction.Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип транзакции не может быть null");
        }
        return transactions.stream()
                .filter(tx -> tx.getType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalByCategory(String category, Transaction.Type type) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Тип транзакции не может быть null");
        }
        return transactions.stream()
                .filter(tx -> tx.getCategory().equals(category) && tx.getType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public boolean hasCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        return categories.containsKey(category);
    }

    public Set<String> getAllCategories() {
        return Collections.unmodifiableSet(categories.keySet());
    }

    public void removeCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
        categories.remove(category);
    }

    public void addCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
        categories.put(category, new Category(category));
    }

    public void renameCategory(String oldName, String newName) {
        if (oldName == null || newName == null ||
                oldName.trim().isEmpty() || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Названия категорий не могут быть пустыми");
        }
        if (!categories.containsKey(oldName)) {
            throw new NoSuchElementException("Категория '" + oldName + "' не найдена");
        }
        if (categories.containsKey(newName)) {
            throw new IllegalStateException("Категория '" + newName + "' уже существует");
        }
        categories.put(newName, categories.remove(oldName));
    }

    public void removeTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Транзакция не может быть null");
        }
        if (transactions.remove(transaction)) {
            updateBalanceAfterRemoval(transaction);
        }
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма транзакции должна быть больше нуля");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
    }

    private void updateBalanceAfterRemoval(Transaction transaction) {
        switch (transaction.getType()) {
            case INCOME:
                balance -= transaction.getAmount();
                totalIncome -= transaction.getAmount();
                break;
            case EXPENSE:
                balance += transaction.getAmount();
                totalExpense -= transaction.getAmount();
                break;
        }
    }

    public void recalculateTotals() {
        totalIncome = 0.0;
        totalExpense = 0.0;
        balance = 0.0;

        for (Transaction transaction : transactions) {
            switch (transaction.getType()) {
                case INCOME:
                    totalIncome += transaction.getAmount();
                    balance += transaction.getAmount();
                    break;
                case EXPENSE:
                    totalExpense += transaction.getAmount();
                    balance -= transaction.getAmount();
                    break;
            }
        }
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    public List<String> getSortedCategories() {
        return categories.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public double getTotalIncomeForYear(int year) {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .filter(t -> t.getDate() != null && t.getDate().getYear() == year)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpenseForYear(int year) {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .filter(t -> t.getDate() != null && t.getDate().getYear() == year)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getBalanceAtStartOfMonth(int year, int month) {
        return transactions.stream()
                .filter(t -> t.getDate() != null)
                .filter(t -> {
                    int transactionYear = t.getDate().getYear();
                    int transactionMonth = t.getDate().getMonthValue();
                    return transactionYear < year || (transactionYear == year && transactionMonth < month);
                })
                .mapToDouble(t -> t.getType() == Transaction.Type.INCOME ? t.getAmount() : -t.getAmount())
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Double.compare(wallet.balance, balance) == 0 &&
                Double.compare(wallet.totalExpense, totalExpense) == 0 &&
                Double.compare(wallet.totalIncome, totalIncome) == 0 &&
                Objects.equals(transactions, wallet.transactions) &&
                Objects.equals(categories, wallet.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, totalExpense, totalIncome, transactions, categories);
    }
}
