package org.malenikajkat.service.persistence;

import org.malenikajkat.model.User;

public interface UserService {
    void saveUser(User user);
    User findUserByEmail(String email);
}