package model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Budget;

class BudgetIntegrationTest {

    private List<Budget> budgets;

    @BeforeEach
    void setUp() {
        budgets = new ArrayList<>();
        budgets.add(new Budget("Продукты", 1000.0));
        budgets.add(new Budget("Транспорт", 500.0));
        budgets.add(new Budget("Развлечения", 300.0));
    }

    @Test
    void testMultipleBudgets_addSpentAndCheckStatus() {
        budgets.get(0).addSpent(850.0);
        budgets.get(1).addSpent(600.0);
        budgets.get(2).addSpent(200.0);

        assertThat(budgets.get(0).isNearLimit()).isTrue();
        assertThat(budgets.get(0).isOverBudget()).isFalse();

        assertThat(budgets.get(1).isOverBudget()).isTrue();

        assertThat(budgets.get(2).isNearLimit()).isFalse();
    }

    @Test
    void testTotalSpentAcrossBudgets() {
        budgets.get(0).addSpent(400.0);
        budgets.get(1).addSpent(300.0);
        budgets.get(2).addSpent(150.0);

        double totalSpent = budgets.stream()
                .mapToDouble(Budget::getSpent)
                .sum();

        assertThat(totalSpent).isEqualTo(850.0);
    }

    @Test
    void testTotalRemainingAcrossBudgets() {
        budgets.get(0).addSpent(200.0);
        budgets.get(1).addSpent(100.0);
        budgets.get(2).addSpent(50.0);

        double totalRemaining = budgets.stream()
                .mapToDouble(Budget::getRemaining)
                .sum();

        assertThat(totalRemaining).isEqualTo(1450.0);
    }

    @Test
    void testFindBudgetByCategory() {
        Budget found = budgets.stream()
                .filter(b -> b.getCategory().equals("Транспорт"))
                .findFirst()
                .orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getLimit()).isEqualTo(500.0);
        assertThat(found.getSpent()).isZero();
    }

    @Test
    void testResetAllBudgets() {
        for (Budget b : budgets) {
            b.addSpent(b.getLimit() * 0.5);
        }

        for (Budget b : budgets) {
            b.resetSpent();
        }

        for (Budget b : budgets) {
            assertThat(b.getSpent()).isZero();
            assertThat(b.getRemaining()).isEqualTo(b.getLimit());
        }
    }

    @Test
    void testMixedOperations_addAndReset() {
        Budget food = budgets.get(0);

        food.addSpent(200.0);
        assertThat(food.getSpent()).isEqualTo(200.0);
        assertThat(food.isNearLimit()).isFalse();

        food.addSpent(600.0);
        assertThat(food.getSpent()).isEqualTo(800.0);
        assertThat(food.isNearLimit()).isTrue();

        food.resetSpent();
        assertThat(food.getSpent()).isZero();
        assertThat(food.isNearLimit()).isFalse();
        assertThat(food.isOverBudget()).isFalse();
    }

    @Test
    void testEdgeCase_spentEqualsLimit() {
        Budget budget = new Budget("Тест", 100.0);
        budget.addSpent(100.0);

        assertThat(budget.getSpent()).isEqualTo(100.0);
        assertThat(budget.getRemaining()).isZero();
        assertThat(budget.isOverBudget()).isFalse();
        assertThat(budget.isNearLimit()).isTrue();
    }

    @Test
    void testToStringFormat() {
        Budget budget = new Budget("Одежда", 200.0);
        budget.addSpent(75.5);

        String expected = "Одежда: лимит 200.00, потрачено 75.50, остаток 124.50";
        assertThat(budget.toString()).isEqualTo(expected);
    }

    @Test
    void testBudgetEquality_sameCategoryDifferentLimits() {
        Budget b1 = new Budget("Книги", 500.0);
        Budget b2 = new Budget("Книги", 1000.0);

        assertThat(b1).isEqualTo(b2);  // Равны по категории
        assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
    }

    @Test
    void testBudgetEquality_differentCategoriesSameLimit() {
        Budget b1 = new Budget("Кафе", 300.0);
        Budget b2 = new Budget("Кино", 300.0);

        assertThat(b1).isNotEqualTo(b2);
        assertThat(b1.hashCode()).isNotEqualTo(b2.hashCode());
    }
}

