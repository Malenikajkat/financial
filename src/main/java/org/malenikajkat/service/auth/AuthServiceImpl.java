package org.malenikajkat.service.auth;

import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.util.PasswordHasher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuthServiceImpl implements AuthService {

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> passwords = new HashMap<>(); // login -> hashedPassword
    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Override
    public void register(String login, String email, String password) throws ServiceException {
        validateLogin(login);
        validateEmail(email);
        validatePassword(password);

        if (userExists(login)) {
            throw new ServiceException("Пользователь с логином '" + login + "' уже существует");
        }

        User user = new User(login, email);
        users.put(login, user);
        passwords.put(login, passwordHasher.hash(password));

        System.out.println("Пользователь " + login + " успешно зарегистрирован");
    }

    @Override
    public User authenticate(String login, String password) throws ServiceException {
        if (!userExists(login)) {
            throw new ServiceException("Пользователь не найден");
        }

        String storedHash = passwords.get(login);
        if (!passwordHasher.verify(password, storedHash)) {
            throw new ServiceException("Неверный пароль");
        }

        return users.get(login);
    }

    @Override
    public boolean userExists(String login) {
        Objects.requireNonNull(login, "Логин не может быть null");
        return users.containsKey(login.trim());
    }

    @Override
    public User getUser(String login) {
        Objects.requireNonNull(login, "Логин не может быть null");
        return users.get(login.trim());
    }

    private void validateLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new ServiceException("Логин не может быть пустым");
        }
        if (login.trim().length() < 3) {
            throw new ServiceException("Логин должен содержать не менее 3 символов");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ServiceException("Email не может быть пустым");
        }
        String trimmedEmail = email.trim();
        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ServiceException("Некорректный формат email: " + trimmedEmail);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ServiceException("Пароль не может быть пустым");
        }
        if (password.trim().length() < 6) {
            throw new ServiceException("Пароль должен содержать не менее 6 символов");
        }
    }
}
