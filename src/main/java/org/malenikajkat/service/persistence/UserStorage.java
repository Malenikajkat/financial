package org.malenikajkat.service.persistence;

import org.malenikajkat.model.User;

public interface UserStorage {
    void addUser(String login, String email, String passwordHash);
    User getUser(String login);
    boolean containsUser(String login);
}