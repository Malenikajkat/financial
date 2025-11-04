package org.malenikajkat.service.persistence;

import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;

import java.util.List;
import java.util.Optional;

public interface DataPersistenceService {

    void saveUser(User user) throws ServiceException;

    Optional<User> loadUser(String login) throws ServiceException;

    boolean userExists(String login) throws ServiceException;

    List<User> getAllUsers() throws ServiceException;

    void deleteUser(String login) throws ServiceException;

    void updateUser(User user) throws ServiceException;
}
