package org.malenikajkat.exception;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static final String INVALID_INPUT_DATA = "Некорректные входные данные.";
    public static final String FIELD_REQUIRED_ERROR = "Обязательное поле отсутствует.";

    public static ValidationException invalidInputData() {
        return new ValidationException(INVALID_INPUT_DATA);
    }

    public static ValidationException requiredFieldError(String fieldName) {
        return new ValidationException(FIELD_REQUIRED_ERROR + " Поле: " + fieldName);
    }
}