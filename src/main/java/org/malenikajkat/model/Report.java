package org.malenikajkat.model;

import java.util.List;
import java.util.Map;

public class Report {
    private final Map<String, Double> incomeByCategory;
    private final double totalIncome;
    private final Map<String, Double> expenseByCategory;
    private final double totalExpense;
    private final List<Budget> budgetDetails;

    public Report(Map<String, Double> incomeByCategory, double totalIncome,
                  Map<String, Double> expenseByCategory, double totalExpense,
                  List<Budget> budgetDetails) {
        this.incomeByCategory = incomeByCategory;
        this.totalIncome = totalIncome;
        this.expenseByCategory = expenseByCategory;
        this.totalExpense = totalExpense;
        this.budgetDetails = budgetDetails;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Финансовый отчёт:\n");
        sb.append("Доходы:\n");
        incomeByCategory.forEach((k, v) -> sb.append(String.format("  %s: %.2f\n", k, v)));
        sb.append(String.format("Итого доходы: %.2f\n", totalIncome));
        sb.append("Расходы:\n");
        expenseByCategory.forEach((k, v) -> sb.append(String.format("  %s: %.2f\n", k, v)));
        sb.append(String.format("Итого расходы: %.2f\n", totalExpense));
        sb.append("Бюджеты:\n");
        budgetDetails.forEach(b -> sb.append("  ").append(b).append("\n"));
        return sb.toString();
    }
}
