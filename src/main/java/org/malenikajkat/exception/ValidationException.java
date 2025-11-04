package org.malenikajkat.exception;

public class ValidationException extends Exception {

    private final String fieldName;
    private final String invalidValue;
    private final String errorDescription;

    public ValidationException(String fieldName, String invalidValue, String errorDescription) {
        super(String.format("Ошибка в поле \"%s\": %s (получено: %s)", fieldName, errorDescription, invalidValue));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.errorDescription = errorDescription;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}