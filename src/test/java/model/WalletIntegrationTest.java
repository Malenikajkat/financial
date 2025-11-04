package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.model.Wallet;

class WalletIntegrationTest {

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.addIncome(1000.0, "Зарплата", LocalDate.now());
        wallet.addIncome(200.0, "Подарок", LocalDate.now());
        wallet.addExpense(300.0, "Аренда", LocalDate.now());
        wallet.addExpense(150.0, "Еда", LocalDate.now());
        wallet.addExpense(50.0, "Транспорт", LocalDate.now());
    }

    @Test
    void testFullWalletWorkflow() {
        assertThat(wallet.getBalance()).isEqualTo(700.0);
        assertThat(wallet.getTransactions()).hasSize(5);

        List<Transaction> incomes = wallet.getIncomeTransactions();
        List<Transaction> expenses = wallet.getExpenseTransactions();

        assertThat(incomes).hasSize(2);
        assertThat(expenses).hasSize(3);

        double totalIncome = wallet.getTotalAmountByType(Transaction.Type.INCOME);
        double totalExpense = wallet.getTotalAmountByType(Transaction.Type.EXPENSE);

        assertThat(totalIncome).isEqualTo(1200.0);
        assertThat(totalExpense).isEqualTo(500.0);

        List<Transaction> rentTransactions = wallet.getTransactionsByCategory("Аренда");
        List<Transaction> foodTransactions = wallet.getTransactionsByCategory("Еда");

        assertThat(rentTransactions).hasSize(1);
        assertThat(foodTransactions).hasSize(1);
        assertThat(rentTransactions.get(0).getAmount()).isEqualTo(300.0);
        assertThat(foodTransactions.get(0).getAmount()).isEqualTo(150.0);

        double totalFoodExpenses = wallet.getTotalByCategory("Еда", Transaction.Type.EXPENSE);
        assertThat(totalFoodExpenses).isEqualTo(150.0);

        assertThat(wallet.hasCategory("Аренда")).isTrue();
        assertThat(wallet.hasCategory("Транспорт")).isTrue();
        assertThat(wallet.getAllCategories()).hasSize(5);

        wallet.renameCategory("Подарок", "Бонус");
        assertThat(wallet.hasCategory("Подарок")).isFalse();
        assertThat(wallet.hasCategory("Бонус")).isTrue();

        List<Transaction> bonusTransactions = wallet.getTransactionsByCategory("Бонус");
        assertThat(bonusTransactions).hasSize(1);
        assertThat(bonusTransactions.get(0).getAmount()).isEqualTo(200.0);

        Transaction transportTx = wallet.getTransactions().stream()
                .filter(t -> t.getCategory().equals("Транспорт"))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Transport transaction not found."));


        wallet.removeTransaction(transportTx);

        assertThat(wallet.getTransactions()).hasSize(4);
        assertThat(wallet.getBalance()).isEqualTo(750.0);

        wallet.clearTransactions();


        assertThat(wallet.getBalance()).isZero();
        assertThat(wallet.getTransactions()).isEmpty();
        assertThat(wallet.getIncomeTransactions()).isEmpty();
        assertThat(wallet.getExpenseTransactions()).isEmpty();
    }

    @Test
    void testEdgeCases() {
        Wallet emptyWallet = new Wallet();

        assertThat(emptyWallet.getBalance()).isZero();
        assertThat(emptyWallet.getTransactions()).isEmpty();
        assertThat(emptyWallet.getTotalAmountByType(Transaction.Type.INCOME)).isZero();
        assertThat(emptyWallet.getTotalAmountByType(Transaction.Type.EXPENSE)).isZero();

        assertThat(emptyWallet.getAllCategories()).isEmpty();

        List<Transaction> unknownCategory = emptyWallet.getTransactionsByCategory("Неизвестная");
        assertThat(unknownCategory).isEmpty();


        double unknownTotalIncome = emptyWallet.getTotalByCategory("Неизвестная", Transaction.Type.INCOME);
        double unknownTotalExpense = emptyWallet.getTotalByCategory("Неизвестная", Transaction.Type.EXPENSE);

        assertThat(unknownTotalIncome).isZero();
        assertThat(unknownTotalExpense).isZero();
    }

    @Test
    void testExceptionScenarios() {
        assertThrows(NoSuchElementException.class, () -> {
            wallet.renameCategory("Не существует", "Новое имя");
        });

        wallet.addCategory("Старая");
        wallet.addCategory("Новая");
        assertThrows(IllegalStateException.class, () -> {
            wallet.renameCategory("Старая", "Новая");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            wallet.removeTransaction(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            wallet.addIncome(-100.0, "Доход", LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            wallet.addExpense(-50.0, "Расход", LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            wallet.addIncome(100.0, "", LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            wallet.addExpense(50.0, null, LocalDate.now());
        });
    }
}
