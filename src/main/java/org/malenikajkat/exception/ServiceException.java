package org.malenikajkat.exception;

public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public static final String USER_NOT_FOUND_ERROR = "Пользователь не найден.";
    public static final String AUTHENTICATION_FAILED_ERROR = "Провал аутентификации.";
    public static final String INTERNAL_SERVER_ERROR = "Внутренняя ошибка сервера.";

    public static ServiceException userNotFound(int userId) {
        return new ServiceException(USER_NOT_FOUND_ERROR + ", User ID: " + userId);
    }

    public static ServiceException authenticationFailed() {
        return new ServiceException(AUTHENTICATION_FAILED_ERROR);
    }
}