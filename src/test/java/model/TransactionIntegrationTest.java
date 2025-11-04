package model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Transaction;

class TransactionIntegrationTest {

    private final LocalDate TODAY = LocalDate.now();
    private final LocalDate YESTERDAY = TODAY.minusDays(1);

    @Test
    void testTransactionsInList_filterByType() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction(1000, "Зарплата", "Доход", Transaction.Type.INCOME, TODAY),
                new Transaction(500, "Еда", "Продукты", Transaction.Type.EXPENSE, TODAY),
                new Transaction(200, "Такси", "Транспорт", Transaction.Type.EXPENSE, YESTERDAY),
                new Transaction(3000, "Бонус", "Премия", Transaction.Type.INCOME, YESTERDAY)
        );

        List<Transaction> incomes = transactions.stream()
                .filter(Transaction::isIncome)
                .collect(Collectors.toList());

        assertThat(incomes).hasSize(2);
        assertThat(incomes)
                .extracting(Transaction::getAmount)
                .containsExactly(1000.0, 3000.0);

        List<Transaction> expenses = transactions.stream()
                .filter(tx -> !tx.isIncome())
                .collect(Collectors.toList());

        assertThat(expenses).hasSize(2);
        assertThat(expenses)
                .extracting(Transaction::getCategory)
                .containsExactly("Продукты", "Транспорт");
    }


    @Test
    void testTransactionsInMap_groupByDate() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction(1000, "Зарплата", "Доход", Transaction.Type.INCOME, TODAY),
                new Transaction(500, "Обед", "Еда", Transaction.Type.EXPENSE, TODAY),
                new Transaction(200, "Кофе", "Напитки", Transaction.Type.EXPENSE, YESTERDAY),
                new Transaction(3000, "Бонус", "Премия", Transaction.Type.INCOME, YESTERDAY)
        );

        Map<LocalDate, List<Transaction>> groupedByDate = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getDate));

        assertThat(groupedByDate).hasSize(2);
        assertThat(groupedByDate.get(TODAY)).hasSize(2);
        assertThat(groupedByDate.get(YESTERDAY)).hasSize(2);
    }

    @Test
    void testTransactionsInSet_uniqueTransactions() {
        Set<Transaction> transactionSet = new HashSet<>();

        Transaction tx1 = new Transaction(100, "Покупка", "Книги", Transaction.Type.EXPENSE, TODAY);
        Transaction tx2 = new Transaction(100, "Покупка", "Книги", Transaction.Type.EXPENSE, TODAY);
        Transaction tx3 = new Transaction(200, "Подарок", "Развлечения", Transaction.Type.EXPENSE, TODAY);

        transactionSet.add(tx1);
        transactionSet.add(tx2);
        transactionSet.add(tx3);

        assertThat(transactionSet).hasSize(2);
        assertThat(transactionSet).contains(tx1, tx3);
    }

    @Test
    void testCalculateTotalAmountByType() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction(1000, "Зарплата", "Доход", Transaction.Type.INCOME, TODAY),
                new Transaction(500, "Еда", "Продукты", Transaction.Type.EXPENSE, TODAY),
                new Transaction(300, "Транспорт", "Такси", Transaction.Type.EXPENSE, TODAY),
                new Transaction(2000, "Продажа", "Активы", Transaction.Type.INCOME, TODAY)
        );

        double totalIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(tx -> !tx.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();

        assertThat(totalIncome).isEqualTo(3000);
        assertThat(totalExpense).isEqualTo(800);
    }

    @Test
    void testFindLatestTransaction() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction(1000, "Зарплата", "Доход", Transaction.Type.INCOME, YESTERDAY),
                new Transaction(500, "Еда", "Продукты", Transaction.Type.EXPENSE, TODAY),
                new Transaction(200, "Кофе", "Напитки", Transaction.Type.EXPENSE, TODAY.minusDays(2))
        );

        Optional<Transaction> latest = transactions.stream()
                .max(Comparator.comparing(Transaction::getDate));

        assertThat(latest).isPresent();
        assertThat(latest.get().getDate()).isEqualTo(TODAY);
        assertThat(latest.get().getDescription()).isEqualTo("Еда");
    }

    @Test
    void testAggregateByCategory() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction(100, "Хлеб", "Еда", Transaction.Type.EXPENSE, TODAY),
                new Transaction(200, "Молоко", "Еда", Transaction.Type.EXPENSE, TODAY),
                new Transaction(50, "Автобус", "Транспорт", Transaction.Type.EXPENSE, TODAY),
                new Transaction(300, "Поезд", "Транспорт", Transaction.Type.EXPENSE, TODAY)
        );

        Map<String, Double> totalByCategory = transactions.stream()
                .collect(Collectors.toMap(
                        Transaction::getCategory,
                        Transaction::getAmount,
                        Double::sum
                ));

        assertThat(totalByCategory).hasSize(2);
        assertThat(totalByCategory.get("Еда")).isEqualTo(300);
        assertThat(totalByCategory.get("Транспорт")).isEqualTo(350);
    }

    @Test
    void testToStringFormatInCollection() {
        Transaction tx = new Transaction(1500.50, "Премия", "Бонус", Transaction.Type.INCOME, TODAY);
        List<String> formatted = Collections.singletonList(tx.toString());

        assertThat(formatted)
                .containsExactly("1500.50 руб. (Доход: Бонус)");
    }

    @Test
    void testTransactionInDataProcessingPipeline() {
        List<Transaction> rawTransactions = Arrays.asList(
                new Transaction(1000, "Зарплата", "Доход", Transaction.Type.INCOME, TODAY),
                new Transaction(200, "Кафе", "Еда", Transaction.Type.EXPENSE, TODAY),
                new Transaction(50, "Проезд", "Транспорт", Transaction.Type.EXPENSE, TODAY)
        );

        Map<String, Double> expenseSummary = rawTransactions.stream()
                .filter(tx -> tx.getType() == Transaction.Type.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        assertThat(expenseSummary).hasSize(2);
        assertThat(expenseSummary.get("Еда")).isEqualTo(200);
        assertThat(expenseSummary.get("Транспорт")).isEqualTo(50);
    }
}
