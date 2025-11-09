package org.malenikajkat.model;

import java.util.List;
import java.util.Map;
import java.util.Locale;

public class Report {
    private final Map<String, Double> incomeByCategory;
    private final double totalIncome;
    private final Map<String, Double> expenseByCategory;
    private final double totalExpense;
    private final List<Budget> budgetDetails;
    private final List<Transaction> transactions;

    public Report(
            Map<String, Double> incomeByCategory,
            double totalIncome,
            Map<String, Double> expenseByCategory,
            double totalExpense,
            List<Budget> budgetDetails,
            List<Transaction> transactions
    ) {
        this.incomeByCategory = incomeByCategory;
        this.totalIncome = totalIncome;
        this.expenseByCategory = expenseByCategory;
        this.totalExpense = totalExpense;
        this.budgetDetails = budgetDetails;
        this.transactions = transactions;
    }

    public Map<String, Double> getIncomeByCategory() {
        return incomeByCategory;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public Map<String, Double> getExpenseByCategory() {
        return expenseByCategory;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public List<Budget> getBudgetDetails() {
        return budgetDetails;
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        StringBuilder report = new StringBuilder();
        report.append("Финансовый отчет:\n");
        appendSection(report, "Доходы:", incomeByCategory, totalIncome);
        appendSection(report, "Расходы:", expenseByCategory, totalExpense);
        appendBudgets(report);
        return report.toString();
    }

    private void appendSection(StringBuilder builder, String sectionTitle, Map<String, Double> values, double total) {
        builder.append(sectionTitle).append("\n");

        values.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(e -> {
                    builder.append("  ").append(e.getKey())
                            .append(": ")
                            .append(String.format(Locale.US, "%.2f", e.getValue()))
                            .append("\n");
                });

        builder.append("Итого: ").append(String.format(Locale.US, "%.2f", total)).append("\n");
    }

    private void appendBudgets(StringBuilder builder) {
        builder.append("Бюджеты:\n");

        budgetDetails.stream()
                .sorted((b1, b2) -> b1.getCategory().compareTo(b2.getCategory()))
                .forEachOrdered(budget -> {
                    builder.append("  ").append(budget.getCategory())
                            .append(": лимит ")
                            .append(String.format(Locale.US, "%.2f", budget.getLimit()))
                            .append(", потрачено ")
                            .append(String.format(Locale.US, "%.2f", budget.getSpent()))
                            .append(", остаток ")
                            .append(String.format(Locale.US, "%.2f", budget.getRemaining()))
                            .append("\n");
                });
    }
}