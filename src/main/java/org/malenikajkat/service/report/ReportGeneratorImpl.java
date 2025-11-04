package org.malenikajkat.service.report;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGeneratorImpl implements ReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String generateSummaryReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        validateUserAndDates(user, startDate, endDate);

        List<Transaction> periodTransactions = filterTransactionsByDate(user, startDate, endDate);
        double totalIncome = calculateTotalIncome(periodTransactions);
        double totalExpense = calculateTotalExpense(periodTransactions);
        double balance = totalIncome - totalExpense;

        return String.format(
                ("СВОДНЫЙ ОТЧЁТ\n" +
                        "Пользователь: %s\n" +
                        "Период: %s – %s\n" +
                        "---------------------------\n" +
                        "Общий доход: %.2f\n" +
                        "Общие расходы: %.2f\n" +
                        "Итоговый баланс: %.2f\n" +
                        "---------------------------\n" +
                        "Всего транзакций: %d"),
                user.getLogin(),
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER),
                totalIncome,
                totalExpense,
                balance,
                periodTransactions.size()
        );
    }

    @Override
    public String generateCategoryReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        validateUserAndDates(user, startDate, endDate);

        List<Transaction> periodTransactions = filterTransactionsByDate(user, startDate, endDate);
        Map<String, Double> incomeByCategory = calculateIncomeByCategory(periodTransactions);
        Map<String, Double> expenseByCategory = calculateExpenseByCategory(periodTransactions);

        StringBuilder report = new StringBuilder();
        report.append(String.format(
                "ДЕТАЛИЗИРОВАННЫЙ ОТЧЁТ ПО КАТЕГОРИЯМ\n" +
                        "Пользователь: %s\n" +
                        "Период: %s – %s\n" +
                        "=================================\n\n",
                user.getLogin(),
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)
        ));

        if (!incomeByCategory.isEmpty()) {
            report.append("ДОХОДЫ ПО КАТЕГОРИЯМ:\n");
            for (Map.Entry<String, Double> entry : incomeByCategory.entrySet()) {
                report.append(String.format("  %s: %.2f\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (!expenseByCategory.isEmpty()) {
            report.append("РАСХОДЫ ПО КАТЕГОРИЯМ:\n");
            for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
                report.append(String.format("  %s: %.2f\n", entry.getKey(), entry.getValue()));
            }
        }

        return report.toString();
    }

    @Override
    public String generateBalanceTrendReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        validateUserAndDates(user, startDate, endDate);


        List<Transaction> periodTransactions = filterTransactionsByDate(user, startDate, endDate);
        Map<LocalDate, Double> dailyBalance = calculateDailyBalance(periodTransactions, startDate, endDate);


        StringBuilder report = new StringBuilder();
        report.append(String.format(
                "ОТЧЁТ О ДИНАМИКЕ БАЛАНСА\n" +
                        "Пользователь: %s\n" +
                        "Период: %s – %s\n" +
                        "==========================\n\n",
                user.getLogin(),
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)
        ));

        for (Map.Entry<LocalDate, Double> entry : dailyBalance.entrySet()) {
            report.append(String.format("%s: %.2f\n", entry.getKey().format(DATE_FORMATTER), entry.getValue()));
        }

        return report.toString();
    }

    @Override
    public String generateExpenseDistributionReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        validateUserAndDates(user, startDate, endDate);

        List<Transaction> periodTransactions = filterTransactionsByDate(user, startDate, endDate);
        Map<String, Double> expenseByCategory = calculateExpenseByCategory(periodTransactions);
        double totalExpense = expenseByCategory.values().stream().mapToDouble(Double::doubleValue).sum();


        if (totalExpense == 0) {
            return String.format(
                    ("ОТЧЁТ О РАСПРЕДЕЛЕНИИ РАСХОДОВ\n" +
                            "Пользователь: %s\n" +
                            "Период: %s – %s\n" +
                            "==============================\n\n" +
                            "За указанный период расходов не зафиксировано."),
                    user.getLogin(),
                    startDate.format(DATE_FORMATTER),
                    endDate.format(DATE_FORMATTER)
            );
        }

        StringBuilder report = new StringBuilder();
        report.append(String.format
                ("ОТЧЁТ О РАСПРЕДЕЛЕНИИ РАСХОДОВ\n" +
                                "Пользователь: %s\n" +
                                "Период: %s – %s\n" +
                                "==============================\n\n",
                        user.getLogin(),
                        startDate.format(DATE_FORMATTER),
                        endDate.format(DATE_FORMATTER)
                ));

        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            double percentage = (entry.getValue() / totalExpense) * 100;
            report.append(String.format("  %s: %.2f (%.1f%%)\n", entry.getKey(), entry.getValue(), percentage));
        }

        report.append(String.format("\nИтого расходы: %.2f", totalExpense));
        return report.toString();
    }

    @Override
    public String generateCompactReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        validateUserAndDates(user, startDate, endDate);

        List<Transaction> periodTransactions = filterTransactionsByDate(user, startDate, endDate);
        double totalIncome = calculateTotalIncome(periodTransactions);
        double totalExpense = calculateTotalExpense(periodTransactions);
        double balance = totalIncome - totalExpense;

        return String.format("Отчёт %s (%s–%s): доход=%.2f, расход=%.2f, баланс=%.2f",
                user.getLogin(),
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER),
                totalIncome,
                totalExpense,
                balance
        );
    }

    private void validateUserAndDates(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }
        if (startDate == null || endDate == null) {
            throw new ServiceException("Даты периода не могут быть null");
        }
        if (startDate.isAfter(endDate)) {
            throw new ServiceException("Начальная дата не может быть позже конечной");
        }
    }

    private List<Transaction> filterTransactionsByDate(User user, LocalDate startDate, LocalDate endDate) {
        return user.getWallet().getTransactions().stream()
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    private double calculateTotalExpense(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private Map<String, Double> calculateIncomeByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isIncome)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    private Map<String, Double> calculateExpenseByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> !t.isIncome())
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    private Map<LocalDate, Double> calculateDailyBalance(
            List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> balanceMap = new LinkedHashMap<>();
        double runningBalance = 0.0;

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            balanceMap.put(currentDate, 0.0);
            currentDate = currentDate.plusDays(1);
        }

        List<Transaction> sortedTransactions = transactions.stream()
                .sorted(ComparatorByDate())
                .toList();

        for (Transaction transaction : sortedTransactions) {
            LocalDate date = transaction.getDate();
            if (balanceMap.containsKey(date)) {
                runningBalance += transaction.isIncome()
                        ? transaction.getAmount()
                        : -transaction.getAmount();
                balanceMap.put(date, runningBalance);
            }
        }

        return balanceMap;
    }

    private Comparator<Transaction> comparatorByDate() {
        return Comparator.comparing(Transaction::getDate);
    }
}
