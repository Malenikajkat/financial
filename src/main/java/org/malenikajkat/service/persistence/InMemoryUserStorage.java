package org.malenikajkat.service.persistence;

import org.malenikajkat.model.User;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserStorage implements UserStorage {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public void addUser(String login, String email, String passwordHash) {
        User user = new User(login, email, passwordHash);
        users.put(login, user);
    }

    @Override
    public User getUser(String login) {
        return users.get(login);
    }

    @Override
    public boolean containsUser(String login) {
        return users.containsKey(login);
    }
}
