package model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Category;

class CategoryUnitTest {

    @Test
    void testConstructor_validName() {
        Category category = new Category("Продукты");
        assertThat(category.getName()).isEqualTo("Продукты");
    }

    @Test
    void testConstructor_nullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Category(null);
        });
    }

    @Test
    void testConstructor_emptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Category("");
        });
    }

    @Test
    void testConstructor_whitespaceOnly() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Category("   ");
        });
    }

    @Test
    void testSetName_valid() {
        Category category = new Category("Еда");
        category.setName("Продукты");
        assertThat(category.getName()).isEqualTo("Продукты");
    }

    @Test
    void testSetName_null() {
        Category category = new Category("Еда");
        assertThrows(IllegalArgumentException.class, () -> {
            category.setName(null);
        });
    }

    @Test
    void testSetName_empty() {
        Category category = new Category("Еда");
        assertThrows(IllegalArgumentException.class, () -> {
            category.setName("");
        });
    }

    @Test
    void testSetName_whitespaceOnly() {
        Category category = new Category("Еда");
        assertThrows(IllegalArgumentException.class, () -> {
            category.setName("   ");
        });
    }

    @Test
    void testSetName_trimmed() {
        Category category = new Category("  Продукты  ");
        assertThat(category.getName()).isEqualTo("Продукты");

        category.setName("  Напитки  ");
        assertThat(category.getName()).isEqualTo("Напитки");
    }

    @Test
    void testEquals_sameObject() {
        Category c1 = new Category("Еда");
        assertThat(c1).isEqualTo(c1);
    }

    @Test
    void testEquals_null() {
        Category c1 = new Category("Еда");
        assertThat(c1).isNotEqualTo(null);
    }

    @Test
    void testEquals_differentClass() {
        Category c1 = new Category("Еда");
        assertThat(c1).isNotEqualTo("Еда");
    }

    @Test
    void testEquals_caseInsensitive() {
        Category c1 = new Category("еда");
        Category c2 = new Category("ЕДА");
        Category c3 = new Category("Еда");

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).isEqualTo(c3);
        assertThat(c2).isEqualTo(c3);
    }

    @Test
    void testEquals_differentNames() {
        Category c1 = new Category("Еда");
        Category c2 = new Category("Транспорт");
        assertThat(c1).isNotEqualTo(c2);
    }

    @Test
    void testHashCode_caseInsensitive() {
        Category c1 = new Category("еда");
        Category c2 = new Category("ЕДА");
        Category c3 = new Category("Еда");

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        assertThat(c1.hashCode()).isEqualTo(c3.hashCode());
    }

    @Test
    void testToString() {
        Category category = new Category("Развлечения");
        assertThat(category.toString()).isEqualTo("Развлечения");
    }
}
