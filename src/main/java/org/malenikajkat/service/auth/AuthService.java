package org.malenikajkat.service.auth;

import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;

public interface AuthService {

    void register(String login, String email, String password) throws ServiceException;

    User authenticate(String login, String password) throws ServiceException;

    boolean userExists(String login);

    User getUser(String login);
}
