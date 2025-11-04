package model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Budget;
import org.malenikajkat.model.Report;
import org.malenikajkat.model.Transaction;

class ReportIntegrationTest {

    @Test
    void testFullReportIntegration() {
        Map<String, Double> income = new HashMap<>();
        income.put("Зарплата", 40000.0);
        income.put("Бонус", 10000.0);

        Map<String, Double> expense = new HashMap<>();
        expense.put("Аренда", 15000.0);
        expense.put("Еда", 8000.0);
        expense.put("Транспорт", 2500.0);

        List<Budget> budgets = new ArrayList<>();
        budgets.add(new Budget("Еда", 10000.0));
        budgets.add(new Budget("Развлечения", 3000.0));

        List<Transaction> txns = new ArrayList<>();
        txns.add(new Transaction(40000.0, "Доход", "Зарплата", Transaction.Type.INCOME, LocalDate.of(2025, 10, 1)));
        txns.add(new Transaction(15000.0, "Расход", "Аренда", Transaction.Type.EXPENSE, LocalDate.of(2025, 10, 5)));
        txns.add(new Transaction(9000.0, "Расход", "Еда", Transaction.Type.EXPENSE, LocalDate.of(2025, 10, 10)));

        Report report = new Report(income, 50000.0, expense, 25500.0, budgets, txns);

        assertThat(report.getIncomeByCategory()).hasSize(2);
        assertThat(report.getTotalIncome()).isEqualTo(50000.0);
        assertThat(report.getExpenseByCategory()).hasSize(3);
        assertThat(report.getTotalExpense()).isEqualTo(25500.0);
        assertThat(report.getBudgetDetails()).hasSize(2);
        assertThat(report.getAllTransactions()).hasSize(3);

        String str = report.toString();
        assertThat(str).contains("Финансовый отчет:");
        assertThat(str).contains("Зарплата: 40000.0");
        assertThat(str).contains("Бонус: 10000.0");
        assertThat(str).contains("Аренда: 15000.0");
        assertThat(str).contains("Еда: 8000.0");
        assertThat(str).contains("Транспорт: 2500.0");
        assertThat(str).contains("Итого: 25500.0");
        assertThat(str).contains("Бюджеты:");
        assertThat(str).contains("Еда: лимит 10000.00, потрачено 0.00, остаток 10000.00");
        assertThat(str).contains("Развлечения: лимит 3000.00, потрачено 0.00, остаток 3000.00");
    }

    @Test
    void testReportWithSingleIncomeAndExpense() {
        Map<String, Double> income = new HashMap<>();
        income.put("Подарок", 5000.0);

        Map<String, Double> expense = new HashMap<>();
        expense.put("Кофе", 500.0);

        List<Budget> budgets = Collections.emptyList();
        List<Transaction> transactions = Collections.singletonList(
                new Transaction(500.0, "Расход", "Кофе", Transaction.Type.EXPENSE, LocalDate.now())
        );

        Report report = new Report(income, 5000.0, expense, 500.0, budgets, transactions);

        String str = report.toString();

        assertThat(report.getIncomeByCategory()).hasSize(1);
        assertThat(report.getTotalIncome()).isEqualTo(5000.0);
        assertThat(report.getExpenseByCategory()).hasSize(1);
        assertThat(report.getTotalExpense()).isEqualTo(500.0);
        assertThat(report.getBudgetDetails()).isEmpty();
        assertThat(report.getAllTransactions()).hasSize(1);

        assertThat(str).contains("Доходы:\nПодарок: 5000.0\nИтого: 5000.0");
        assertThat(str).contains("Расходы:\nКофе: 500.0\nИтого: 500.0");
        assertThat(str).contains("Бюджеты:\n");
    }

    @Test
    void testReportWithOverBudgetCategory() {
        Budget foodBudget = new Budget("Еда", 8000.0);
        foodBudget.addSpent(9000.0);

        List<Budget> budgets = Collections.singletonList(foodBudget);

        Map<String, Double> income = Collections.singletonMap("Зарплата", 60000.0);
        Map<String, Double> expense = Collections.singletonMap("Еда", 9000.0);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(60000.0, "Доход", "Зарплата", Transaction.Type.INCOME, LocalDate.now()));
        transactions.add(new Transaction(9000.0, "Расход", "Еда", Transaction.Type.EXPENSE, LocalDate.now()));


        Report report = new Report(income, 60000.0, expense, 9000.0, budgets, transactions);

        String str = report.toString();

        assertThat(str).contains("Еда: лимит 8000.00, потрачено 9000.00, остаток -1000.00");
        assertThat(str).contains("Расходы:\nЕда: 9000.0\nИтого: 9000.0");
    }

    @Test
    void testReportWithNearLimitCategory() {
        Budget entertainmentBudget = new Budget("Развлечения", 10000.0);
        entertainmentBudget.addSpent(8000.0);


        List<Budget> budgets = Collections.singletonList(entertainmentBudget);

        Map<String, Double> income = Collections.singletonMap("Фриланс", 20000.0);
        Map<String, Double> expense = Collections.singletonMap("Развлечения", 8000.0);

        List<Transaction> transactions = Collections.singletonList(
                new Transaction(8000.0, "Расход", "Развлечения", Transaction.Type.EXPENSE, LocalDate.now())
        );

        Report report = new Report(income, 20000.0, expense, 8000.0, budgets, transactions);

        String str = report.toString();

        assertThat(str).contains("Развлечения: лимит 10000.00, потрачено 8000.00, остаток 2000.00");
        assertThat(str).contains("Расходы:\nРазвлечения: 8000.0\nИтого: 8000.0");
    }

    @Test
    void testLargeNumberOfTransactions() {
        Map<String, Double> income = new HashMap<>();
        income.put("Основной доход", 100000.0);

        Map<String, Double> expense = new HashMap<>();
        expense.put("Продукты", 12000.0);
        expense.put("Транспорт", 3000.0);
        expense.put("Связь", 500.0);
        expense.put("Развлечения", 2000.0);

        List<Budget> budgets = new ArrayList<>();
        budgets.add(new Budget("Продукты", 1500.0));
        budgets.add(new Budget("Транспорт", 400.0));

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            transactions.add(new Transaction(
                    200.0,
                    "Расход",
                    "Мелкие траты",
                    Transaction.Type.EXPENSE,
                    LocalDate.now().minusDays(i)
            ));
        }

        Report report = new Report(income, 100000.0, expense, 1800.0, budgets, transactions);

        assertThat(report.getAllTransactions()).hasSize(50);
        assertThat(report.getExpenseByCategory()).hasSize(4);
        assertThat(report.getBudgetDetails()).hasSize(2);

        String str = report.toString();
        assertThat(str).contains("Основной доход: 100000.0");
        assertThat(str).contains("Продукты: 1200.0");
        assertThat(str).contains("Транспорт: 300.0");
        assertThat(str).contains("Связь: 500.0");
        assertThat(str).contains("Развлечения: 200.0");
        assertThat(str).contains("Итого: 1800.0");
    }
}
