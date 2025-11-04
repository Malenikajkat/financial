package org.malenikajkat.model;

public class AuthenticationResult {
    private final boolean success;
    private final String message;
    private final User authenticatedUser;

    public AuthenticationResult(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.authenticatedUser = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    @Override
    public String toString() {
        return "AuthenticationResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", authenticatedUser=" + authenticatedUser +
                '}';
    }
}