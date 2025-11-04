package org.malenikajkat.service.report;

import org.malenikajkat.model.*;
import org.malenikajkat.exception.ServiceException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportGeneratorImpl implements ReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Comparator<Transaction> TRANSACTION_COMPARATOR_BY_DATE = Comparator.comparing(Transaction::getDate);

    @Override
    public Report generateSummaryReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        List<Transaction> periodTransactions = getFilteredTransactions(user, startDate, endDate);

        double totalIncome = periodTransactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = periodTransactions.stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        return createBasicReport(user, startDate, endDate, totalIncome, totalExpense, balance, periodTransactions);
    }

    @Override
    public Report generateCategoryReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        List<Transaction> periodTransactions = getFilteredTransactions(user, startDate, endDate);

        Map<String, Double> incomeByCategory = periodTransactions.stream()
                .filter(Transaction::isIncome)
                .collect(Collectors.groupingBy(
                        categoryOrUnknown(Transaction::getCategory),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Double> expenseByCategory = periodTransactions.stream()
                .filter(t -> !t.isIncome())
                .collect(Collectors.groupingBy(
                        categoryOrUnknown(Transaction::getCategory),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        return new Report(incomeByCategory, totalIncome(incomeByCategory), expenseByCategory, totalExpense(expenseByCategory), List.of(), periodTransactions);
    }

    @Override
    public Report generateBalanceTrendReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        List<Transaction> periodTransactions = getFilteredTransactions(user, startDate, endDate);

        Map<LocalDate, Double> dailyBalance = calculateDailyBalance(periodTransactions, startDate, endDate);

        double totalIncome = totalIncome(getIncomeByCategory(periodTransactions));
        double totalExpense = totalExpense(getExpenseByCategory(periodTransactions));

        return new Report(null, totalIncome, null, totalExpense, List.of(), periodTransactions);
    }
    @Override
    public Report generateExpenseDistributionReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        List<Transaction> periodTransactions = getFilteredTransactions(user, startDate, endDate);

        Map<String, Double> expenseByCategory = periodTransactions.stream()
                .filter(t -> !t.isIncome())
                .collect(Collectors.groupingBy(
                        categoryOrUnknown(Transaction::getCategory),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        double totalExpense = totalExpense(expenseByCategory);

        return new Report(
                null,
                0.0,
                expenseByCategory,
                totalExpense,
                List.of(),
                periodTransactions
        );
    }

    @Override
    public Report generateCompactReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException {
        List<Transaction> periodTransactions = getFilteredTransactions(user, startDate, endDate);

        double totalIncome = periodTransactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = periodTransactions.stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        return createBasicReport(user, startDate, endDate, totalIncome, totalExpense, balance, periodTransactions);
    }

    @Override
    public Report generateReport(User user) throws ServiceException {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        LocalDate today = LocalDate.now();
        return generateSummaryReport(user, oneYearAgo, today);
    }

    private Report createBasicReport(User user, LocalDate startDate, LocalDate endDate,
                                     double totalIncome, double totalExpense, double balance,
                                     List<Transaction> transactions) {
        return new Report(
                null,
                totalIncome,
                null,
                totalExpense,
                List.of(),
                transactions
        );
    }

    private List<Transaction> getFilteredTransactions(User user, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Даты начала и окончания не могут быть null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }

        return user.getWallet().getTransactions().stream()
                .filter(transaction -> !transaction.getDate().isBefore(startDate) &&
                        !transaction.getDate().isAfter(endDate))
                .sorted(TRANSACTION_COMPARATOR_BY_DATE)
                .collect(Collectors.toList());
    }

    private Function<Transaction, String> categoryOrUnknown(Function<Transaction, String> getter) {
        return transaction -> {
            String category = getter.apply(transaction);
            return (category == null || category.trim().isEmpty()) ? "Без категории" : category.trim();
        };
    }

    private Map<LocalDate, Double> calculateDailyBalance(List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> balanceMap = new TreeMap<>();
        double runningBalance = 0.0;

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            balanceMap.put(currentDate, 0.0);
            currentDate = currentDate.plusDays(1);
        }

        for (Transaction transaction : transactions) {
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

    private double totalIncome(Map<String, Double> incomeByCategory) {
        return incomeByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    private double totalExpense(Map<String, Double> expenseByCategory) {
        return expenseByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    private Map<String, Double> getIncomeByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isIncome)
                .collect(Collectors.groupingBy(
                        categoryOrUnknown(Transaction::getCategory),
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    private Map<String, Double> getExpenseByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> !t.isIncome())
                .collect(Collectors.groupingBy(
                        categoryOrUnknown(Transaction::getCategory),
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }
}
