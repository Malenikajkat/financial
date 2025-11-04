package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.model.Wallet;

class WalletUnitTest {

    private Wallet wallet;
    private Transaction incomeTx;
    private Transaction expenseTx;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        incomeTx = new Transaction(100.0, "Зарплата", "Доходы", Transaction.Type.INCOME, LocalDate.now());
        expenseTx = new Transaction(50.0, "Продукты", "Расходы", Transaction.Type.EXPENSE, LocalDate.now());
    }

    @Test
    void testGetBalance_initial() {
        assertThat(wallet.getBalance()).isEqualTo(0.0);
    }

    @Test
    void testAddIncome_updatesBalance() {
        wallet.addIncome(100.0, "Зарплата", LocalDate.now());
        assertThat(wallet.getBalance()).isEqualTo(100.0);
        assertThat(wallet.getTransactions()).hasSize(1);
        assertThat(wallet.getIncomeTransactions()).hasSize(1);
    }

    @Test
    void testAddExpense_updatesBalance() {
        wallet.addExpense(50.0, "Продукты", LocalDate.now());
        assertThat(wallet.getBalance()).isEqualTo(-50.0);
        assertThat(wallet.getTransactions()).hasSize(1);
        assertThat(wallet.getExpenseTransactions()).hasSize(1);
    }

    @Test
    void testGetTransactionsByCategory() {
        wallet.addTransaction(incomeTx);
        wallet.addTransaction(expenseTx);

        List<Transaction> incomes = wallet.getTransactionsByCategory("Доходы");
        assertThat(incomes).hasSize(1);
        assertThat(incomes.get(0).getCategory()).isEqualTo("Доходы");

        List<Transaction> expenses = wallet.getTransactionsByCategory("Расходы");
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getCategory()).isEqualTo("Расходы");
    }

    @Test
    void testGetIncomeTransactions() {
        wallet.addTransaction(incomeTx);
        wallet.addTransaction(expenseTx);

        List<Transaction> incomes = wallet.getIncomeTransactions();
        assertThat(incomes).hasSize(1);
        assertThat(incomes.get(0).getType()).isEqualTo(Transaction.Type.INCOME);
    }

    @Test
    void testGetExpenseTransactions() {
        wallet.addTransaction(incomeTx);
        wallet.addTransaction(expenseTx);

        List<Transaction> expenses = wallet.getExpenseTransactions();
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getType()).isEqualTo(Transaction.Type.EXPENSE);
    }

    @Test
    void testClearTransactions_resetsBalance() {
        wallet.addIncome(100.0, "Доход", LocalDate.now());
        wallet.clearTransactions();

        assertThat(wallet.getBalance()).isZero();
        assertThat(wallet.getTransactions()).isEmpty();
    }

    @Test
    void testGetTotalAmountByType() {
        wallet.addTransaction(new Transaction(200.0, "Бонус", "Доходы", Transaction.Type.INCOME, LocalDate.now()));
        wallet.addTransaction(new Transaction(30.0, "Кофе", "Развлечения", Transaction.Type.EXPENSE, LocalDate.now()));

        double totalIncome = wallet.getTotalAmountByType(Transaction.Type.INCOME);
        double totalExpense = wallet.getTotalAmountByType(Transaction.Type.EXPENSE);

        assertThat(totalIncome).isEqualTo(200.0);
        assertThat(totalExpense).isEqualTo(30.0);
    }

    @Test
    void testGetTotalByCategory() {
        wallet.addTransaction(new Transaction(100.0, "Еда", "Продукты", Transaction.Type.EXPENSE, LocalDate.now()));
        wallet.addTransaction(new Transaction(50.0, "Фрукты", "Продукты", Transaction.Type.EXPENSE, LocalDate.now()));

        double total = wallet.getTotalByCategory("Продукты", Transaction.Type.EXPENSE);
        assertThat(total).isEqualTo(150.0);
    }

    @Test
    void testHasCategory() {
        wallet.addCategory("Продукты");
        assertThat(wallet.hasCategory("Продукты")).isTrue();
        assertThat(wallet.hasCategory("Одежда")).isFalse();
    }

    @Test
    void testGetAllCategories() {
        wallet.addCategory("Еда");
        wallet.addCategory("Транспорт");
        Set<String> categories = wallet.getAllCategories();
        assertThat(categories).containsExactlyInAnyOrder("Еда", "Транспорт");
    }

    @Test
    void testRemoveCategory() {
        wallet.addCategory("Книги");
        wallet.removeCategory("Книги");
        assertThat(wallet.getAllCategories()).doesNotContain("Книги");
    }

    @Test
    void testRenameCategory_success() {
        wallet.addCategory("Старый");
        wallet.renameCategory("Старый", "Новый");
        assertThat(wallet.hasCategory("Старый")).isFalse();
        assertThat(wallet.hasCategory("Новый")).isTrue();
    }

    @Test
    void testRenameCategory_throwsIfOldNotExists() {
        assertThrows(NoSuchElementException.class, () -> {
            wallet.renameCategory("Не существует", "Новое");
        });
    }

    @Test
    void testRenameCategory_throwsIfNewExists() {
        wallet.addCategory("Существующий");
        assertThrows(IllegalStateException.class, () -> {
            wallet.renameCategory("Существующий", "Существующий");
        });
    }

    @Test
    void testRemoveTransaction() {
        wallet.addTransaction(incomeTx);
        wallet.removeTransaction(incomeTx);
        assertThat(wallet.getTransactions()).isEmpty();
    }

    @Test
    void testRemoveTransaction_throwsIfNull() {
        assertThrows(NullPointerException.class, () -> {
            wallet.removeTransaction(null);
        });
    }

    @Test
    void testEquals_andHashCode() {
        Wallet w1 = new Wallet();
        w1.addIncome(50.0, "Тест", LocalDate.now());

        Wallet w2 = new Wallet();
        w2.addIncome(50.0, "Тест", LocalDate.now());

        assertThat(w1.getBalance()).isEqualTo(w2.getBalance());
        assertThat(w1.getTransactions()).hasSameSizeAs(w2.getTransactions());
        assertThat(w1).isEqualTo(w2);
        assertThat(w1.hashCode()).isEqualTo(w2.hashCode());
    }
}
