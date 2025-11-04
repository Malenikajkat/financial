package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Budget;
import org.malenikajkat.model.Category;
import org.malenikajkat.model.User;
import org.malenikajkat.model.Wallet;
import org.mindrot.jbcrypt.BCrypt;

class UserUnitTest {

    private User user;
    private Wallet wallet;
    private Category category;
    private Budget budget;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        category = new Category("Продукты");
        budget = new Budget("Продукты", 5000.0);
        user = new User("testuser", "test@example.com", "$2a$10$hYpDfuWJqQl.XFugGvAFLurJkCExPZLOw8RuDB7XyOwTkTdfTQzLi"); // valid_hash
    }

    @Test
    void testGetters() {
        assertThat(user.getLogin()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("$2a$10$hYpDfuWJqQl.XFugGvAFLurJkCExPZLOw8RuDB7XyOwTkTdfTQzLi");
    }

    @Test
    void testAddCategory() {
        user.addCategory(category);
        assertThat(user.hasCategory("Продукты")).isTrue();
        assertThat(user.getCategory("Продукты")).isEqualTo(category);
    }

    @Test
    void testAddCategory_throwsIfNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.addCategory(null);
        });
    }

    @Test
    void testRemoveCategory() {
        user.addCategory(category);
        user.removeCategory("Продукты");
        assertThat(user.hasCategory("Продукты")).isFalse();
        assertThat(user.getCategory("Продукты")).isNull();
    }

    @Test
    void testRemoveCategory_throwsIfEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.removeCategory("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.removeCategory(null);
        });
    }

    @Test
    void testHasCategory() {
        user.addCategory(category);
        assertThat(user.hasCategory("Продукты")).isTrue();
        assertThat(user.hasCategory("Одежда")).isFalse();
    }

    @Test
    void testHasCategory_throwsIfEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.hasCategory("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.hasCategory(null);
        });
    }

    @Test
    void testGetCategory() {
        user.addCategory(category);
        assertThat(user.getCategory("Продукты")).isEqualTo(category);
        assertThat(user.getCategory("Одежда")).isNull();
    }

    @Test
    void testGetCategory_throwsIfEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.getCategory("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.getCategory(null);
        });
    }

    @Test
    void testSetEmail() {
        user.setEmail("new@example.com");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void testSetEmail_throwsOnInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail("invalid-email");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail(null);
        });
    }

    @Test
    void testGetBudgets() {
        user.addBudget(budget);
        Map<String, Budget> budgets = user.getBudgets();
        assertThat(budgets).hasSize(1);
        assertThat(budgets.get("Продукты")).isEqualTo(budget);
    }

    @Test
    void testAddBudget() {
        user.addBudget(budget);
        assertThat(user.hasBudget("Продукты")).isTrue();
        assertThat(user.getBudget("Продукты")).isEqualTo(budget);
    }

    @Test
    void testAddBudget_throwsIfNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.addBudget(null);
        });
    }

    @Test
    void testGetBudget() {
        user.addBudget(budget);
        assertThat(user.getBudget("Продукты")).isEqualTo(budget);
        assertThat(user.getBudget("Жильё")).isNull();
    }

    @Test
    void testGetBudget_throwsIfEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.getBudget("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.getBudget(null);
        });
    }

    @Test
    void testHasBudget() {
        user.addBudget(budget);
        assertThat(user.hasBudget("Продукты")).isTrue();
        assertThat(user.hasBudget("Жильё")).isFalse();
    }

    @Test
    void testHasBudget_throwsIfEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.hasBudget("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.hasBudget(null);
        });
    }

    @Test
    void testRemoveBudget() {
        user.addBudget(budget);
        user.removeBudget("Продукты");
        assertThat(user.hasBudget("Продукты")).isFalse();
        assertThat(user.getBudget("Продукты")).isNull();
    }

    @Test
    void testRemoveBudget_throwsIfEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.removeBudget("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.removeBudget(null);
        });
    }

    @Test
    void testVerifyPassword() {
        String password = "correct_password";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User userWithHash = new User("user", "user@example.com", hash);
        assertThat(userWithHash.verifyPassword(password)).isTrue();
        assertThat(userWithHash.verifyPassword("wrong_password")).isFalse();
    }

    @Test
    void testEquals_andHashCode() {
        User u1 = new User("alice", "a@example.com", "hash");
        User u2 = new User("alice", "b@example.com", "hash");
        User u3 = new User("bob", "b@example.com", "hash");

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
        assertThat(u1).isNotEqualTo(u3);
    }

    @Test
    void testToString() {
        String expected = "User{login='testuser', email='test@example.com', categoriesCount=0}";
        assertThat(user.toString()).isEqualTo(expected);
    }

    @Test
    void testConstructor_validatesLogin() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("", "email@example.com", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User("ab", "email@example.com", "password");
        });
        new User("abc", "email@example.com", "password");
    }

    @Test
    void testConstructor_validatesEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("user", "", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User("user", "invalid-email", "password");
        });
        new User("user", "valid@example.com", "password");
    }

    @Test
    void testConstructor_withNullValues() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User(null, "email@example.com", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User("user", null, "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User("user", "email@example.com", null);
        });
    }

    @Test
    void testJsonCreatorConstructor() {
        Wallet wallet = new Wallet();
        Category category = new Category("Развлечения");
        Budget budget = new Budget("Развлечения", 2000.0);

        User user = new User(
                "jsonuser",
                "json@example.com",
                "hashed_password",
                wallet,
                Collections.singletonList(category),
                Map.of("Развлечения", budget)
        );

        assertThat(user.getLogin()).isEqualTo("jsonuser");
        assertThat(user.getEmail()).isEqualTo("json@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashed_password");
        assertThat(user.getWallet()).isSameAs(wallet);
        assertThat(user.hasCategory("Развлечения")).isTrue();
        assertThat(user.hasBudget("Развлечения")).isTrue();
    }

    @Test
    void testJsonCreatorConstructor_nullCollections() {
        User user = new User(
                "user",
                "email@example.com",
                "hash",
                new Wallet(),
                null,
                null
        );

        assertThat(user.getCategories()).isEmpty();
        assertThat(user.getBudgets()).isEmpty();
    }

    @Test
    void testGetCategories() {
        user.addCategory(category);
        List<Category> categories = user.getCategories();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0)).isEqualTo(category);
    }

    @Test
    void testPasswordValidation_inConstructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("user", "email@example.com", "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User("user2", "email2@example.com", null);
        });
    }

    @Test
    void testCategoryNameTrim() {
        user.addCategory(new Category("  Продукты  "));
        assertThat(user.hasCategory("Продукты")).isTrue();
        assertThat(user.getCategory("Продукты").getName()).isEqualTo("Продукты");
    }

    @Test
    void testBudgetCategoryTrim() {
        Budget budgetWithSpaces = new Budget("  Жильё  ", 10000.0);
        user.addBudget(budgetWithSpaces);
        assertThat(user.hasBudget("Жильё")).isTrue();
        assertThat(user.getBudget("Жильё").getCategory()).isEqualTo("Жильё");
    }

    @Test
    void testWalletInitialization() {
        assertThat(user.getWallet()).isNotNull();
        assertThat(user.getWallet().getTransactions()).isEmpty();
        assertThat(user.getWallet().getBalance()).isEqualTo(0.0);
    }

    @Test
    void testMultipleCategoriesAndBudgets() {
        Category cat1 = new Category("Еда");
        Category cat2 = new Category("Транспорт");
        user.addCategory(cat1);
        user.addCategory(cat2);

        Budget b1 = new Budget("Еда", 3000.0);
        Budget b2 = new Budget("Транспорт", 1500.0);
        user.addBudget(b1);
        user.addBudget(b2);

        assertThat(user.getCategories()).hasSize(2);
        assertThat(user.getBudgets()).hasSize(2);
        assertThat(user.hasCategory("Еда")).isTrue();
        assertThat(user.hasCategory("Транспорт")).isTrue();
        assertThat(user.hasBudget("Еда")).isTrue();
        assertThat(user.hasBudget("Транспорт")).isTrue();
    }
}