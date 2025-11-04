package org.malenikajkat.util;

import org.malenikajkat.exception.ValidationException;

import java.util.regex.Pattern;

public class ValidatorService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
    );

    private static final Pattern AMOUNT_PATTERN = Pattern.compile("\\d+(?:\\.\\d{1,2})?"); // Положительное число с дробью до двух знаков

    private void validateRegex(String input, Pattern pattern, String fieldName) throws ValidationException {
        if (!pattern.matcher(input).matches()) {
            throw new ValidationException(fieldName, input, "Формат не соответствует требуемому");
        }
    }

    public void validateLength(String input, String fieldName, int minLength, int maxLength) throws ValidationException {
        if (input.length() < minLength || input.length() > maxLength) {
            throw new ValidationException(fieldName, input, "Длина должна быть от " + minLength + " до " + maxLength + " символов");
        }
    }

    public void validateEmail(String email, String fieldName) throws ValidationException {
        validateRegex(email, EMAIL_PATTERN, fieldName);
    }

    public void validatePassword(String password, String fieldName) throws ValidationException {
        validateRegex(password, PASSWORD_PATTERN, fieldName);
    }

    public void validateAmount(String amount, String fieldName) throws ValidationException {
        validateRegex(amount, AMOUNT_PATTERN, fieldName);
    }

    public void validateNonEmpty(String input, String fieldName) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName, input, "Поле не может быть пустым");
        }
    }

    public void validateRange(double value, String fieldName, double minValue, double maxValue) throws ValidationException {
        if (value < minValue || value > maxValue) {
            throw new ValidationException(fieldName, String.valueOf(value), "Значение должно быть в диапазоне от " + minValue + " до " + maxValue);
        }
    }

    public void validateInteger(String input, String fieldName) throws ValidationException {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName, input, "Значение должно быть целым числом");
        }
    }

    public void validatePositiveFloat(String input, String fieldName) throws ValidationException {
        try {
            float num = Float.parseFloat(input);
            if (num <= 0) {
                throw new ValidationException(fieldName, input, "Значение должно быть положительным числом");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName, input, "Значение должно быть числом");
        }
    }

    public void validatePositiveInteger(String input, String fieldName) throws ValidationException {
        try {
            int num = Integer.parseInt(input);
            if (num <= 0) {
                throw new ValidationException(fieldName, input, "Значение должно быть положительным целым числом");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName, input, "Значение должно быть целым числом");
        }
    }
}