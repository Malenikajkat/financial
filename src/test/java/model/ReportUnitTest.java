package model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Budget;
import org.malenikajkat.model.Report;
import org.malenikajkat.model.Transaction;

class ReportUnitTest {

    private Map<String, Double> incomeByCategory;
    private Map<String, Double> expenseByCategory;
    private List<Budget> budgetDetails;
    private List<Transaction> transactions;
    private Report report;

    @BeforeEach
    void setUp() {
        incomeByCategory = new HashMap<>();
        incomeByCategory.put("Зарплата", 50000.0);
        incomeByCategory.put("Фриланс", 15000.0);

        expenseByCategory = new HashMap<>();
        expenseByCategory.put("Продукты", 10000.0);
        expenseByCategory.put("Транспорт", 3000.0);

        budgetDetails = new ArrayList<>();
        budgetDetails.add(new Budget("Продукты", 12000.0));
        budgetDetails.add(new Budget("Развлечения", 5000.0));

        transactions = new ArrayList<>();
        transactions.add(new Transaction(5000.0, "Доход", "Зарплата", Transaction.Type.INCOME, LocalDate.now()));
        transactions.add(new Transaction(2000.0, "Расход", "Продукты", Transaction.Type.EXPENSE, LocalDate.now()));

        report = new Report(
                incomeByCategory,
                65000.0,
                expenseByCategory,
                13000.0,
                budgetDetails,
                transactions
        );
    }

    @Test
    void testGetIncomeByCategory() {
        assertThat(report.getIncomeByCategory())
                .containsExactlyEntriesOf(incomeByCategory);
    }

    @Test
    void testGetTotalIncome() {
        assertThat(report.getTotalIncome()).isEqualTo(65000.0);
    }

    @Test
    void testGetExpenseByCategory() {
        assertThat(report.getExpenseByCategory())
                .containsExactlyEntriesOf(expenseByCategory);
    }

    @Test
    void testGetTotalExpense() {
        assertThat(report.getTotalExpense()).isEqualTo(13000.0);
    }

    @Test
    void testGetBudgetDetails() {
        assertThat(report.getBudgetDetails()).containsExactlyElementsOf(budgetDetails);
    }

    @Test
    void testGetAllTransactions() {
        assertThat(report.getAllTransactions()).containsExactlyElementsOf(transactions);
    }

    @Test
    void testToString_containsIncome() {
        String reportStr = report.toString();
        assertThat(reportStr).contains("Доходы:");
        assertThat(reportStr).contains("Зарплата: 50000.0");
        assertThat(reportStr).contains("Фриланс: 15000.0");
        assertThat(reportStr).contains("Итого: 65000.0");
    }

    @Test
    void testToString_containsExpense() {
        String reportStr = report.toString();
        assertThat(reportStr).contains("Расходы:");
        assertThat(reportStr).contains("Продукты: 10000.0");
        assertThat(reportStr).contains("Транспорт: 3000.0");
        assertThat(reportStr).contains("Итого: 13000.0");
    }

    @Test
    void testToString_containsBudgets() {
        String reportStr = report.toString();
        assertThat(reportStr).contains("Бюджеты:");
        assertThat(reportStr).contains("Продукты: лимит 12000.00, потрачено 0.00, остаток 12000.00");
        assertThat(reportStr).contains("Развлечения: лимит 5000.00, потрачено 0.00, остаток 5000.00");
    }

    @Test
    void testEmptyIncome() {
        Report emptyIncomeReport = new Report(
                new HashMap<>(),
                0.0,
                expenseByCategory,
                13000.0,
                budgetDetails,
                transactions
        );

        String reportStr = emptyIncomeReport.toString();
        assertThat(reportStr).contains("Доходы:\nИтого: 0.0");
    }

    @Test
    void testEmptyExpense() {
        Report emptyExpenseReport = new Report(
                incomeByCategory,
                65000.0,
                new HashMap<>(),
                0.0,
                budgetDetails,
                transactions
        );

        String reportStr = emptyExpenseReport.toString();
        assertThat(reportStr).contains("Расходы:\nИтого: 0.0");
    }

    @Test
    void testEmptyBudgets() {
        Report noBudgetsReport = new Report(
                incomeByCategory,
                65000.0,
                expenseByCategory,
                13000.0,
                new ArrayList<>(),
                transactions
        );

        String reportStr = noBudgetsReport.toString();
        assertThat(reportStr).contains("Бюджеты:\n");
        assertThat(reportStr.split("Бюджеты:\n")[1]).doesNotContain(":");
    }
}
