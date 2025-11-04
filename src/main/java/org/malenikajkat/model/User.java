package org.malenikajkat.model;

import java.io.Serializable;
import java.util.*;
import org.mindrot.jbcrypt.BCrypt;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;
    private String email;
    private final String passwordHash;
    private final Wallet wallet;
    private final Map<String, Category> categories;
    private final Map<String, Budget> budgets;

    @JsonCreator
    public User(@JsonProperty("login") String login,
                @JsonProperty("email") String email,
                @JsonProperty("passwordHash") String passwordHash,
                @JsonProperty("wallet") Wallet wallet,
                @JsonProperty("categories") Collection<Category> categories,
                @JsonProperty("budgets") Map<String, Budget> budgets) {

        if (login == null || email == null || passwordHash == null) {
            throw new IllegalArgumentException("Логин, email и пароль обязательны!");
        }

        validateLogin(login);
        validateEmail(email);
        validatePassword(passwordHash);

        this.login = login.trim();
        this.email = email.trim();
        this.passwordHash = passwordHash;
        this.wallet = wallet != null ? wallet : new Wallet();
        this.categories = new HashMap<>();
        if (categories != null) {
            for (Category category : categories) {
                this.categories.put(category.getName(), category);
            }
        }
        this.budgets = budgets != null ? new HashMap<>(budgets) : new HashMap<>();
    }

    public User(String login, String email, String passwordHash) {
        validateLogin(login);
        validateEmail(email);
        validatePassword(passwordHash);

        this.login = login.trim();
        this.email = email.trim();
        this.passwordHash = passwordHash;
        this.wallet = new Wallet();
        this.categories = new HashMap<>();
        this.budgets = new HashMap<>();
    }

    public String getLogin() { return login; }
    public String getEmail() { return email; }
    public Wallet getWallet() { return wallet; }
    public String getPasswordHash() { return passwordHash; }


    public List<Category> getCategories() {
        return new ArrayList<>(categories.values());
    }

    public void addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Категория не должна быть null");
        }
        categories.put(category.getName(), category);
    }

    public void removeCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым или null");
        }
        categories.remove(categoryName.trim());
    }

    public boolean hasCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым или null");
        }
        return categories.containsKey(categoryName.trim());
    }

    public Category getCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым или null");
        }
        return categories.get(categoryName.trim());
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email.trim();
    }

    public Map<String, Budget> getBudgets() {
        return Collections.unmodifiableMap(budgets);
    }

    public void addBudget(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Бюджет не может быть null");
        }
        budgets.put(budget.getCategory(), budget);
    }

    public Budget getBudget(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория бюджета не может быть пустой или null");
        }
        return budgets.get(category.trim());
    }

    public boolean hasBudget(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория бюджета не может быть пустой или null");
        }
        return budgets.containsKey(category.trim());
    }

    public void removeBudget(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория бюджета не может быть пустой или null");
        }
        budgets.remove(category.trim());
    }

    public boolean verifyPassword(String inputPassword) {
        return BCrypt.checkpw(inputPassword, passwordHash);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User that = (User) obj;
        return login.equals(that.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", categoriesCount=" + categories.size() +
                '}';
    }

    private void validateLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым или null");
        }
        if (login.trim().length() < 3) {
            throw new IllegalArgumentException("Логин должен содержать минимум 3 символа");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail не может быть пустым или null");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Неверный формат e-mail: " + email);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым или null");
        }
        if (password.trim().length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов");
        }
    }
}
