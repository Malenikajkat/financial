package org.malenikajkat.service.auth;

import org.malenikajkat.model.AuthenticationResult;
import org.malenikajkat.model.User;
import org.malenikajkat.service.persistence.UserStorage;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserStorage userStorage;

    public AuthServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void register(String login, String email, String password) {
        if (userStorage.containsUser(login)) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует.");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        userStorage.addUser(login, email, hashedPassword);
    }

    @Override
    public AuthenticationResult authenticate(String login, String password) {
        User user = userStorage.getUser(login);
        if (user == null) {
            log.warn("Пользователь с логином {} не найден.", login);
            return new AuthenticationResult(false, "Пользователь не найден", null);
        }

        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            return new AuthenticationResult(true, "", user);
        } else {
            log.error("Пароль не прошёл проверку: полученный пароль {}, ожидаемый хэш {}", password, user.getPasswordHash());
            return new AuthenticationResult(false, "Неверный пароль", null);
        }
    }

    @Override
    public boolean userExists(String login) {
        return userStorage.containsUser(login);
    }

    @Override
    public User getUser(String login) {
        return userStorage.getUser(login);
    }
}
