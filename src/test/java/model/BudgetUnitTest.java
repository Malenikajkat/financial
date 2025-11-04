package model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Budget;

class BudgetUnitTest {

    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = new Budget("Продукты", 1000.0);
    }

    @Test
    void testConstructor_validParams() {
        assertThat(budget.getCategory()).isEqualTo("Продукты");
        assertThat(budget.getLimit()).isEqualTo(1000.0);
        assertThat(budget.getSpent()).isZero();
    }

    @Test
    void testConstructor_nullCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Budget(null, 500.0);
        });
    }

    @Test
    void testConstructor_emptyCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Budget("   ", 500.0);
        });
    }

    @Test
    void testConstructor_zeroLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Budget("Одежда", 0.0);
        });
    }

    @Test
    void testConstructor_negativeLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Budget("Развлечения", -100.0);
        });
    }

    @Test
    void testGetRemaining() {
        budget.addSpent(300.0);
        assertThat(budget.getRemaining()).isEqualTo(700.0);
    }

    @Test
    void testIsOverBudget_false() {
        budget.addSpent(900.0);
        assertThat(budget.isOverBudget()).isFalse();
    }

    @Test
    void testIsOverBudget_true() {
        budget.addSpent(1100.0);
        assertThat(budget.isOverBudget()).isTrue();
    }

    @Test
    void testIsNearLimit_false() {
        budget.addSpent(700.0);
        assertThat(budget.isNearLimit()).isFalse();
    }

    @Test
    void testIsNearLimit_true() {
        budget.addSpent(800.0);
        assertThat(budget.isNearLimit()).isTrue();
    }

    @Test
    void testAddSpent_positiveAmount() {
        budget.addSpent(250.0);
        assertThat(budget.getSpent()).isEqualTo(250.0);
    }

    @Test
    void testAddSpent_zeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            budget.addSpent(0.0);
        });
    }

    @Test
    void testAddSpent_negativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            budget.addSpent(-50.0);
        });
    }

    @Test
    void testResetSpent() {
        budget.addSpent(400.0);
        budget.resetSpent();
        assertThat(budget.getSpent()).isZero();
        assertThat(budget.getRemaining()).isEqualTo(1000.0);
    }

    @Test
    void testToString() {
        String expected = "Продукты: лимит 1000.00, потрачено 0.00, остаток 1000.00";
        assertThat(budget.toString()).isEqualTo(expected);
    }

    @Test
    void testEquals_sameObject() {
        assertThat(budget).isEqualTo(budget);
    }

    @Test
    void testEquals_null() {
        assertThat(budget).isNotEqualTo(null);
    }

    @Test
    void testEquals_differentClass() {
        assertThat(budget).isNotEqualTo("Бюджет");
    }

    @Test
    void testEquals_sameCategory() {
        Budget other = new Budget("Продукты", 500.0);
        assertThat(budget).isEqualTo(other);
    }

    @Test
    void testEquals_differentCategory() {
        Budget other = new Budget("Одежда", 1000.0);
        assertThat(budget).isNotEqualTo(other);
    }

    @Test
    void testHashCode() {
        Budget other = new Budget("Продукты", 200.0);
        assertThat(budget.hashCode()).isEqualTo(other.hashCode());
    }
}
