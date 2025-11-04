package model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Category;
import org.malenikajkat.model.Transaction;

class CategoryIntegrationTest {

    @Test
    void testCategoryInMap_asKey() {
        Map<Category, Double> expenses = new HashMap<>();

        Category food = new Category("Еда");
        Category TRANSPORT = new Category("Транспорт");
        Category entertainment = new Category("развлечения");

        expenses.put(food, 5000.0);
        expenses.put(TRANSPORT, 2000.0);
        expenses.put(entertainment, 1500.0);

        assertThat(expenses).hasSize(3);

        Category searchFood = new Category("еДА");
        assertThat(expenses.containsKey(searchFood)).isTrue();
        assertThat(expenses.get(searchFood)).isEqualTo(5000.0);
    }

    @Test
    void testCategoryInSet() {
        Set<Category> categories = new HashSet<>();

        categories.add(new Category("Еда"));
        categories.add(new Category("еда"));
        categories.add(new Category("ЕДА"));

        assertThat(categories).hasSize(1);
        assertThat(categories).contains(new Category("еда"));
    }

    @Test
    void testListOfCategories_distinctByName() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Продукты"));
        categoryList.add(new Category("продукты"));
        categoryList.add(new Category("ПРОДУКТЫ"));

        Set<String> uniqueNames = categoryList.stream()
                .map(Category::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        assertThat(uniqueNames).hasSize(1);
        assertThat(uniqueNames).contains("продукты");
    }

    @Test
    void testCategoryAsPartOfTransaction() {
        Transaction tx = new Transaction(
                1000.0,
                "Покупка",
                "Одежда",
                Transaction.Type.EXPENSE,
                LocalDate.now()
        );

        assertThat(tx.getCategory()).isEqualTo("Одежда");

        Transaction otherTx = new Transaction(
                500.0,
                "Ещё покупка",
                "одежда",  // String
                Transaction.Type.EXPENSE,
                LocalDate.now().plusDays(1)
        );

        assertThat(tx.getCategory().equalsIgnoreCase(otherTx.getCategory())).isTrue();
    }

    @Test
    void testSortingCategories() {
        List<Category> categories = Arrays.asList(
                new Category("транспорт"),
                new Category("Еда"),
                new Category("РАЗВЛЕЧЕНИЯ")
        );

        categories.sort(Comparator.comparing(c -> c.getName().toLowerCase()));

        assertThat(categories.get(0).getName()).isEqualTo("Еда");
        assertThat(categories.get(1).getName()).isEqualTo("РАЗВЛЕЧЕНИЯ");
        assertThat(categories.get(2).getName()).isEqualTo("транспорт");
    }
}
