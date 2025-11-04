package org.malenikajkat.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String email;
    private final Wallet wallet;
    private final Map<String, Budget> budgets;

    public User(String login, String email) {
        validateLogin(login);
        validateEmail(email);

        this.login = login.trim();
        this.email = email.trim();
        this.wallet = new Wallet();
        this.budgets = new HashMap<>();
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public Wallet getWallet() {
        return wallet;
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
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
        return budgets.get(category.trim());
    }

    public boolean hasBudget(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
        return budgets.containsKey(category.trim());
    }

    public void removeBudget(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой или null");
        }
        budgets.remove(category.trim());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return login.equals(other.login);
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
                ", wallet=" + wallet +
                '}';
    }

    private void validateLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым или null");
        }
        if (login.trim().length() < 3) {
            throw new IllegalArgumentException("Логин должен содержать не менее 3 символов");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым или null");
        }
        String trimmedEmail = email.trim();
        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Некорректный формат email: " + trimmedEmail);
        }
    }
}
