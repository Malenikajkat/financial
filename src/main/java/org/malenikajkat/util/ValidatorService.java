package org.malenikajkat.util;

import org.malenikajkat.model.User;
import org.malenikajkat.exception.ValidationException;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidatorService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    public void validateEntity(Object entity) throws ValidationException {
        if (entity instanceof User) {
            validateUser((User) entity);
        } else if (entity instanceof LocalDate) {
            validateDate((LocalDate) entity);
        } else if (entity instanceof Number) {
            validatePositiveNumber((Number) entity);
        } else if (entity instanceof String) {
            validateString((String) entity);
        } else {
            throw new ValidationException("Неизвестный тип для валидации");
        }
    }

    public void validateUser(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Пользователь не может быть null");
        }
        validateLogin(user.getLogin());
        validateEmail(user.getEmail());
    }

    public void validateLogin(String login) throws ValidationException {
        if (login == null || login.trim().isEmpty()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (login.length() < 3) {
            throw new ValidationException("Логин должен содержать не менее 3 символов");
        }
        if (!login.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Логин может содержать только буквы, цифры и символ '_'.");
        }
    }

    public void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Некорректный формат email");
        }
    }

    public void validateDate(LocalDate date) throws ValidationException {
        if (date == null) {
            throw new ValidationException("Дата не может быть null");
        }
    }

    public void validateDateRange(LocalDate startDate, LocalDate endDate) throws ValidationException {
        validateDate(startDate);
        validateDate(endDate);
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Начальная дата не может быть позже конечной");
        }
    }

    public void validatePositiveNumber(Number number) throws ValidationException {
        if (number.doubleValue() <= 0) {
            throw new ValidationException("Число должно быть положительным");
        }
    }

    public void validateString(String str) throws ValidationException {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException("Строка не может быть пустой");
        }
    }

    public void validateCategory(String categoryName, String fieldDescription) throws ValidationException {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new ValidationException(fieldDescription + " не может быть пустым");
        }
        if (categoryName.length() < 2) {
            throw new ValidationException(fieldDescription + " должно содержать минимум 2 символа");
        }
        if (categoryName.length() > 50) {
            throw new ValidationException(fieldDescription + " не должно превышать 50 символов");
        }
        if (!categoryName.matches("^[a-zA-Zа-яА-Я0-9 ]+$")) {
            throw new ValidationException(fieldDescription + " содержит недопустимые символы");
        }
    }

    public void validatePositiveAmount(double amount) throws ValidationException {
        if (amount <= 0) {
            throw new ValidationException("Сумма должна быть положительной");
        }
    }

    public void validatePositiveFloat(double value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " должен быть положительным числом");
        }
    }
}