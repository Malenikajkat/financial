package org.malenikajkat.service.persistence;

import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.util.JsonUtils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileDataPersistenceService implements DataPersistenceService {

    private final Path dataDirectory;
    private final JsonUtils jsonUtils;

    public FileDataPersistenceService(Path dataDirectory, JsonUtils jsonUtils) {
        if (dataDirectory == null) {
            throw new IllegalArgumentException("dataDirectory не может быть null");
        }
        if (jsonUtils == null) {
            throw new IllegalArgumentException("jsonUtils не может быть null");
        }

        this.dataDirectory = dataDirectory;
        this.jsonUtils = jsonUtils;

        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            throw new IllegalArgumentException("Не удалось создать директорию данных: " + dataDirectory, e);
        }
    }

    @Override
    public void saveUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }

        Path userFile = getUserFilePath(user.getLogin());
        try (Writer writer = Files.newBufferedWriter(userFile)) {
            jsonUtils.toJson(user, writer);
        } catch (IOException e) {
            throw new ServiceException("Ошибка при сохранении пользователя: " + user.getLogin(), e);
        }
    }

    @Override
    public Optional<User> loadUser(String login) throws ServiceException {
        if (login == null || login.trim().isEmpty()) {
            return Optional.empty();
        }

        Path userFile = getUserFilePath(login);
        if (!Files.exists(userFile)) {
            return Optional.empty();
        }

        try (Reader reader = Files.newBufferedReader(userFile)) {
            User user = jsonUtils.fromJson(reader, User.class);
            return Optional.of(user);
        } catch (IOException e) {
            throw new ServiceException("Ошибка при загрузке пользователя: " + login, e);
        }
    }

    @Override
    public boolean userExists(String login) throws ServiceException {
        if (login == null || login.trim().isEmpty()) {
            return false;
        }

        Path userFile = getUserFilePath(login);
        return Files.exists(userFile);
    }

    @Override
    public List<User> getAllUsers() throws ServiceException {
        try {
            return Files.list(dataDirectory)
                    .filter(Files::isRegularFile)
                    .map(this::loadUserFromFile)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServiceException("Ошибка при получении всех пользователей", e);
        }
    }

    @Override
    public void deleteUser(String login) throws ServiceException {
        if (login == null || login.trim().isEmpty()) {
            throw new ServiceException("Логин не может быть пустым");
        }

        Path userFile = getUserFilePath(login);
        if (!Files.exists(userFile)) {
            throw new ServiceException("Пользователь не найден: " + login);
        }

        try {
            Files.delete(userFile);
        } catch (IOException e) {
            throw new ServiceException("Ошибка при удалении пользователя: " + login, e);
        }
    }

    @Override
    public void updateUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }

        if (!userExists(user.getLogin())) {
            throw new ServiceException("Пользователь не найден: " + user.getLogin());
        }

        saveUser(user);
    }

    private Path getUserFilePath(String login) {
        return dataDirectory.resolve(login + ".json");
    }

    private Optional<User> loadUserFromFile(Path filePath) {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            User user = jsonUtils.fromJson(reader, User.class);
            return Optional.of(user);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + ", причина: " + e.getMessage());
            return Optional.empty();
        }
    }
}
