package model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.malenikajkat.model.Transaction;

class TransactionUnitTest {

    private final LocalDate TODAY = LocalDate.now();

    @Test
    void testConstructor_validData() {
        Transaction tx = new Transaction(
                1000.0,
                "Зарплата",
                "Доход",
                Transaction.Type.INCOME,
                TODAY
        );

        assertThat(tx.getAmount()).isEqualTo(1000.0);
        assertThat(tx.getDescription()).isEqualTo("Зарплата");
        assertThat(tx.getCategory()).isEqualTo("Доход");
        assertThat(tx.getType()).isEqualTo(Transaction.Type.INCOME);
        assertThat(tx.getDate()).isEqualTo(TODAY);
    }

    @Test
    void testConstructor_negativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(-100.0, "Покупка", "Еда", Transaction.Type.EXPENSE, TODAY);
        });
    }

    @Test
    void testConstructor_zeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(0.0, "Покупка", "Еда", Transaction.Type.EXPENSE, TODAY);
        });
    }

    @Test
    void testConstructor_nullDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(100.0, null, "Еда", Transaction.Type.EXPENSE, TODAY);
        });
    }

    @Test
    void testConstructor_emptyDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(100.0, "", "Еда", Transaction.Type.EXPENSE, TODAY);
        });
    }

    @Test
    void testConstructor_whitespaceOnlyDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(100.0, "   ", "Еда", Transaction.Type.EXPENSE, TODAY);
        });
    }

    @Test
    void testConstructor_nullCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(100.0, "Покупка", null, Transaction.Type.EXPENSE, TODAY);
        });
    }

    @Test
    void testConstructor_emptyCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(100.0, "Покупка", "", Transaction.Type.EXPENSE, TODAY);
        });
    }

    @Test
    void testConstructor_whitespaceOnlyCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(100.0, "Покупка", "   ", Transaction.Type.EXPENSE, TODAY);
        });
    }

    @Test
    void testConstructor_nullType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(100.0, "Покупка", "Еда", null, TODAY);
        });
    }

    @Test
    void testGetters() {
        Transaction tx = new Transaction(
                500.50,
                " Кофе ",
                " Напитки ",
                Transaction.Type.EXPENSE,
                TODAY
        );

        assertThat(tx.getAmount()).isEqualTo(500.50);
        assertThat(tx.getDescription()).isEqualTo("Кофе");
        assertThat(tx.getCategory()).isEqualTo("Напитки");
        assertThat(tx.getType()).isEqualTo(Transaction.Type.EXPENSE);
        assertThat(tx.getDate()).isEqualTo(TODAY);
    }

    @Test
    void testIsIncome_incomeType() {
        Transaction tx = new Transaction(1000, "Зарплата", "Доход", Transaction.Type.INCOME, TODAY);
        assertThat(tx.isIncome()).isTrue();
    }

    @Test
    void testIsIncome_expenseType() {
        Transaction tx = new Transaction(500, "Еда", "Продукты", Transaction.Type.EXPENSE, TODAY);
        assertThat(tx.isIncome()).isFalse();
    }

    @Test
    void testGetTypeLabel_income() {
        Transaction tx = new Transaction(1000, "Зарплата", "Доход", Transaction.Type.INCOME, TODAY);
        assertThat(tx.getTypeLabel()).isEqualTo("Доход");
    }

    @Test
    void testGetTypeLabel_expense() {
        Transaction tx = new Transaction(500, "Еда", "Продукты", Transaction.Type.EXPENSE, TODAY);
        assertThat(tx.getTypeLabel()).isEqualTo("Расход");
    }

    @Test
    void testToString_income() {
        Transaction tx = new Transaction(1500.75, "Бонус", "Премия", Transaction.Type.INCOME, TODAY);
        assertThat(tx.toString()).isEqualTo("1500.75 руб. (Доход: Премия)");
    }

    @Test
    void testToString_expense() {
        Transaction tx = new Transaction(300.00, "Обед", "Ресторан", Transaction.Type.EXPENSE, TODAY);
        assertThat(tx.toString()).isEqualTo("300.00 руб. (Расход: Ресторан)");
    }

    @Test
    void testEquals_sameObject() {
        Transaction tx = new Transaction(100, "Тест", "Категория", Transaction.Type.INCOME, TODAY);
        assertThat(tx).isEqualTo(tx);
    }

    @Test
    void testEquals_null() {
        Transaction tx = new Transaction(100, "Тест", "Категория", Transaction.Type.INCOME, TODAY);
        assertThat(tx).isNotEqualTo(null);
    }

    @Test
    void testEquals_differentClass() {
        Transaction tx = new Transaction(100, "Тест", "Категория", Transaction.Type.INCOME, TODAY);
        assertThat(tx).isNotEqualTo("не транзакция");
    }

    @Test
    void testEquals_identicalTransactions() {
        Transaction tx1 = new Transaction(200, "Оплата", "ЖКХ", Transaction.Type.EXPENSE, TODAY);
        Transaction tx2 = new Transaction(200, "Оплата", "ЖКХ", Transaction.Type.EXPENSE, TODAY);
        assertThat(tx1).isEqualTo(tx2);
        assertThat(tx1.hashCode()).isEqualTo(tx2.hashCode());
    }

    @Test
    void testEquals_differentAmount() {
        Transaction tx1 = new Transaction(100, "Тест", "Категория", Transaction.Type.INCOME, TODAY);
        Transaction tx2 = new Transaction(200, "Тест", "Категория", Transaction.Type.INCOME, TODAY);
        assertThat(tx1).isNotEqualTo(tx2);
    }

    @Test
    void testEquals_differentDescription() {
        Transaction tx1 = new Transaction(100, "А", "Категория", Transaction.Type.INCOME, TODAY);
        Transaction tx2 = new Transaction(100, "Б", "Категория", Transaction.Type.INCOME, TODAY);
        assertThat(tx1).isNotEqualTo(tx2);
    }

    @Test
    void testEquals_differentCategory() {
        Transaction tx1 = new Transaction(100, "Тест", "А", Transaction.Type.INCOME, TODAY);
        Transaction tx2 = new Transaction(100, "Тест", "Б", Transaction.Type.INCOME, TODAY);
        assertThat(tx1).isNotEqualTo(tx2);
    }

    @Test
    void testEquals_differentType() {
        Transaction tx1 = new Transaction(100, "Тест", "Категория", Transaction.Type.INCOME, TODAY);
        Transaction tx2 = new Transaction(100, "Тест", "Категория", Transaction.Type.EXPENSE, TODAY);
        assertThat(tx1).isNotEqualTo(tx2);
    }

    @Test
    void testEquals_differentDate() {
        Transaction tx1 = new Transaction(100, "Тест", "Категория", Transaction.Type.INCOME, TODAY);
        Transaction tx2 = new Transaction(100, "Тест", "Категория", Transaction.Type.INCOME, TODAY.plusDays(1));
        assertThat(tx1).isNotEqualTo(tx2);
    }

    @Test
    void testHashCode_identicalTransactions() {
        Transaction tx1 = new Transaction(100, "Оплата", "ЖКХ", Transaction.Type.EXPENSE, TODAY);
        Transaction tx2 = new Transaction(100, "Оплата", "ЖКХ", Transaction.Type.EXPENSE, TODAY);
        assertThat(tx1.hashCode()).isEqualTo(tx2.hashCode());
    }

    @Test
    void testHashCode_differentTransactions() {
        Transaction tx1 = new Transaction(100, "А", "Категория", Transaction.Type.INCOME, TODAY);
        Transaction tx2 = new Transaction(200, "Б", "Другая", Transaction.Type.EXPENSE, TODAY.plusDays(1));
        assertThat(tx1.hashCode()).isNotEqualTo(tx2.hashCode());
    }

    @Test
    void testSerializable_roundTrip() throws Exception {
        Transaction original = new Transaction(
                1000.0,
                "Зарплата",
                "Доход",
                Transaction.Type.INCOME,
                TODAY
        );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        Transaction deserialized = (Transaction) ois.readObject();
        ois.close();

        assertThat(deserialized).isEqualTo(original);
        assertThat(deserialized.getAmount()).isEqualTo(original.getAmount());
        assertThat(deserialized.getDescription()).isEqualTo(original.getDescription());
        assertThat(deserialized.getCategory()).isEqualTo(original.getCategory());
        assertThat(deserialized.getType()).isEqualTo(original.getType());
        assertThat(deserialized.getDate()).isEqualTo(original.getDate());
    }
}
