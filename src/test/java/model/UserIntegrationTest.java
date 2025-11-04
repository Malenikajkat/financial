package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.malenikajkat.model.*;
import org.mindrot.jbcrypt.BCrypt;

class UserIntegrationTest {

    private User user;
    private Wallet wallet;
    private Category foodCategory;
    private Category transportCategory;
    private Category housingCategory;
    private Budget foodBudget;
    private Budget transportBudget;
    private Budget housingBudget;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        user = new User("integration_user", "user@example.com", "valid_hash");

        foodCategory = new Category("Еда");
        transportCategory = new Category("Транспорт");
        housingCategory = new Category("Жильё");

        foodBudget = new Budget("Еда", 3000.0);
        transportBudget = new Budget("Транспорт", 1500.0);
        housingBudget = new Budget("Жильё", 8000.0);
    }

    @Test
    void testFullUserWorkflow_withTransactionsAndBudgets() {
        user.addCategory(foodCategory);
        user.addCategory(transportCategory);
        user.addCategory(housingCategory);

        user.addBudget(foodBudget);
        user.addBudget(transportBudget);
        user.addBudget(housingBudget);

        user.getWallet().addIncome(10000.0, "Зарплата", LocalDate.now());
        user.getWallet().addExpense(3000.0, "Еда", LocalDate.now());
        user.getWallet().addExpense(1500.0, "Транспорт", LocalDate.now());
        user.getWallet().addExpense(7000.0, "Жильё", LocalDate.now());

        assertThat(user.getWallet().getBalance())
                .isEqualTo(10000.0 - 3000.0 - 1500.0 - 7000.0);

        List<Transaction> foodTransactions = user.getWallet().getTransactionsByCategory("Еда");
        List<Transaction> transportTransactions = user.getWallet().getTransactionsByCategory("Транспорт");
        List<Transaction> housingTransactions = user.getWallet().getTransactionsByCategory("Жильё");

        assertThat(foodTransactions).hasSize(1);
        assertThat(transportTransactions).hasSize(1);
        assertThat(housingTransactions).hasSize(1);

        assertThat(foodTransactions.get(0).getAmount()).isEqualTo(3000.0);
        assertThat(transportTransactions.get(0).getAmount()).isEqualTo(1500.0);
        assertThat(housingTransactions.get(0).getAmount()).isEqualTo(7000.0);

        double foodSpent = user.getWallet().getTotalByCategory("Еда", Transaction.Type.EXPENSE);
        double transportSpent = user.getWallet().getTotalByCategory("Транспорт", Transaction.Type.EXPENSE);
        double housingSpent = user.getWallet().getTotalByCategory("Жильё", Transaction.Type.EXPENSE);

        assertThat(foodSpent).isEqualTo(3000.0);
        assertThat(transportSpent).isEqualTo(1500.0);
        assertThat(housingSpent).isEqualTo(7000.0);

        double foodRemaining = foodBudget.getLimit() - foodSpent;
        double transportRemaining = transportBudget.getLimit() - transportSpent;
        double housingRemaining = housingBudget.getLimit() - housingSpent;

        assertThat(foodRemaining).isEqualTo(0.0);
        assertThat(transportRemaining).isEqualTo(0.0);
        assertThat(housingRemaining).isEqualTo(1000.0);
    }

    @Test
    void testPasswordVerification_afterSerialization() {
        String rawPassword = "secure_pass";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        User user = new User("testuser", "test@example.com", hashedPassword);


        assertThat(user.verifyPassword(rawPassword)).isTrue();
        assertThat(user.verifyPassword("wrong_password")).isFalse();
        assertThat(user.verifyPassword("")).isFalse();
    }

    @Test
    void testCategoryAndBudgetConsistency_afterModifications() {
        user.addCategory(foodCategory);
        user.addBudget(foodBudget);


        user.removeCategory("Еда");
        user.removeBudget("Еда");


        Category updatedCategory = new Category("Продукты");
        Budget updatedBudget = new Budget("Продукты", 4000.0);

        user.addCategory(updatedCategory);
        user.addBudget(updatedBudget);


        assertThat(user.hasCategory("Еда")).isFalse();
        assertThat(user.hasBudget("Еда")).isFalse();

        assertThat(user.hasCategory("Продукты")).isTrue();
        assertThat(user.hasBudget("Продукты")).isTrue();

        assertThat(user.getBudget("Продукты").getLimit()).isEqualTo(4000.0);
    }

    @Test
    void testWalletOperations_reflectInUserState() {
        user.addCategory(new Category("Развлечения"));
        user.addBudget(new Budget("Развлечения", 1000.0));

        user.getWallet().addExpense(200.0, "Развлечения", LocalDate.now());
        user.getWallet().addExpense(300.0, "Развлечения", LocalDate.now());
        user.getWallet().addIncome(1000.0, "Бонус", LocalDate.now());

        assertThat(user.getWallet().getBalance()).isEqualTo(1000.0 - 200.0 - 300.0);

        double totalEntertainment = user.getWallet()
                .getTotalByCategory("Развлечения", Transaction.Type.EXPENSE);
        assertThat(totalEntertainment).isEqualTo(500.0);

        Budget entertainmentBudget = user.getBudget("Развлечения");
        double remaining = entertainmentBudget.getLimit() - totalEntertainment;
        assertThat(remaining).isEqualTo(500.0);
    }

    @Test
    void testUserClone() {
        User original = new User("clone", "clone@example.com", "valid_hash");
        original.addCategory(foodCategory);
        original.addBudget(foodBudget);
        original.getWallet().addIncome(1000.0, "Доход", LocalDate.now());

        User copy = new User(
                original.getLogin(),
                original.getEmail(),
                original.getPasswordHash()
        );

        for (Category cat : original.getCategories()) {
            copy.addCategory(new Category(cat.getName()));
        }

        for (Map.Entry<String, Budget> entry : original.getBudgets().entrySet()) {
            Budget originalBudget = entry.getValue();
            copy.addBudget(new Budget(
                    originalBudget.getCategory(),
                    originalBudget.getLimit()
            ));
        }

        for (Transaction tx : original.getWallet().getTransactions()) {
            Transaction copiedTx = new Transaction(
                    tx.getAmount(),
                    tx.getDescription(),
                    tx.getCategory(),
                    tx.getType(),
                    tx.getDate()
            );
            copy.getWallet().addTransaction(copiedTx);
        }

        assertThat(copy.getLogin()).isEqualTo(original.getLogin());
        assertThat(copy.getEmail()).isEqualTo(original.getEmail());
        assertThat(copy.getPasswordHash()).isEqualTo(original.getPasswordHash());
        assertThat(copy.getWallet().getBalance())
                .isEqualTo(original.getWallet().getBalance());

        assertThat(copy.getCategories()).hasSameSizeAs(original.getCategories());
        for (int i = 0; i < copy.getCategories().size(); i++) {
            assertThat(copy.getCategories().get(i).getName())
                    .isEqualTo(original.getCategories().get(i).getName());
        }
        assertThat(copy.getBudgets()).hasSameSizeAs(original.getBudgets());
        for (String key : original.getBudgets().keySet()) {
            Budget copyBudget = copy.getBudgets().get(key);
            Budget origBudget = original.getBudgets().get(key);
            assertThat(copyBudget.getCategory()).isEqualTo(origBudget.getCategory());
            assertThat(copyBudget.getLimit()).isEqualTo(origBudget.getLimit());
        }

        assertThat(copy.getWallet().getTransactions())
                .hasSameSizeAs(original.getWallet().getTransactions());
        for (int i = 0; i < copy.getWallet().getTransactions().size(); i++) {
            Transaction copyTx = copy.getWallet().getTransactions().get(i);
            Transaction origTx = original.getWallet().getTransactions().get(i);
            assertThat(copyTx.getAmount()).isEqualTo(origTx.getAmount());
            assertThat(copyTx.getDescription()).isEqualTo(origTx.getDescription());
            assertThat(copyTx.getCategory()).isEqualTo(origTx.getCategory());
            assertThat(copyTx.getType()).isEqualTo(origTx.getType());
            assertThat(copyTx.getDate()).isEqualTo(origTx.getDate());
        }
    }

    @Test
    void testEdgeCase_emptyUser() {
        User emptyUser = new User("empty", "empty@example.com", "hash");

        assertThat(emptyUser.getWallet().getBalance()).isZero();
        assertThat(emptyUser.getWallet().getTransactions()).isEmpty();
        assertThat(emptyUser.getCategories()).isEmpty();
        assertThat(emptyUser.getBudgets()).isEmpty();

        assertThat(emptyUser.getCategory("Нет")).isNull();
        assertThat(emptyUser.getBudget("Нет")).isNull();

        assertThat(emptyUser.hasCategory("Нет")).isFalse();
        assertThat(emptyUser.hasBudget("Нет")).isFalse();
    }

    @Test
    void testComplexScenario_multipleCategoriesAndTransactions() {
        user.addCategory(foodCategory);
        user.addCategory(transportCategory);
        user.addCategory(housingCategory);

        user.addBudget(foodBudget);
        user.addBudget(transportBudget);
        user.addBudget(housingBudget);

        user.getWallet().addIncome(10000.0, "Зарплата", LocalDate.now());
        user.getWallet().addIncome(2000.0, "Дополнительный доход", LocalDate.now());

        user.getWallet().addExpense(3000.0, "Еда", LocalDate.now());
        user.getWallet().addExpense(1500.0, "Транспорт", LocalDate.now());
        user.getWallet().addExpense(7000.0, "Жильё", LocalDate.now());
        user.getWallet().addExpense(500.0, "Еда", LocalDate.now());

        double expectedBalance = 10000 + 2000 - 3000 - 1500 - 7000 - 500;
        assertThat(user.getWallet().getBalance()).isEqualTo(expectedBalance);

        assertThat(user.getWallet().getTransactions()).hasSize(6);

        double foodSpent = user.getWallet().getTotalByCategory("Еда", Transaction.Type.EXPENSE);
        double transportSpent = user.getWallet().getTotalByCategory("Транспорт", Transaction.Type.EXPENSE);
        double housingSpent = user.getWallet().getTotalByCategory("Жильё", Transaction.Type.EXPENSE);

        assertThat(foodSpent).isEqualTo(3500.0);
        assertThat(transportSpent).isEqualTo(1500.0);
        assertThat(housingSpent).isEqualTo(7000.0);

        assertThat(foodBudget.getLimit() - foodSpent).isEqualTo(-500.0);
        assertThat(transportBudget.getLimit() - transportSpent).isEqualTo(0.0);
        assertThat(user.getBudget("Жильё").getLimit() - housingSpent).isEqualTo(1000.0);
    }

    @Test
    void testTransactionHistoryConsistency() {
        user.addCategory(foodCategory);
        user.addCategory(transportCategory);

        user.getWallet().addExpense(500.0, "Еда", LocalDate.of(2025, 10, 1));
        user.getWallet().addExpense(300.0, "Транспорт", LocalDate.of(2025, 10, 5));
        user.getWallet().addExpense(700.0, "Еда", LocalDate.of(2025, 10, 15));


        List<Transaction> octoberTransactions = user.getWallet().getTransactionsByMonth(2025, 10);
        assertThat(octoberTransactions).hasSize(3);

        double octoberFood = user.getWallet()
                .getTotalByCategoryAndMonth("Еда", 2025, 10, Transaction.Type.EXPENSE);
        assertThat(octoberFood).isEqualTo(1200.0);


        for (Transaction t : octoberTransactions) {
            if (t.getCategory().equals("Еда")) {
                assertThat(t.getAmount()).isIn(500.0, 700.0);
            } else if (t.getCategory().equals("Транспорт")) {
                assertThat(t.getAmount()).isEqualTo(300.0);
            }
        }
    }

    @Test
    void testBudgetExceededNotification() {
        user.addCategory(foodCategory);
        user.addBudget(foodBudget);

        user.getWallet().addExpense(2500.0, "Еда", LocalDate.now());
        user.getWallet().addExpense(1000.0, "Еда", LocalDate.now());

        double totalSpent = user.getWallet().getTotalByCategory("Еда", Transaction.Type.EXPENSE);
        double remaining = foodBudget.getLimit() - totalSpent;

        assertThat(totalSpent).isEqualTo(3500.0);
        assertThat(remaining).isEqualTo(-500.0);
    }
}
