package org.malenikajkat.service.notification;

import org.malenikajkat.model.Transaction;
import org.malenikajkat.model.User;
import org.malenikajkat.util.EmailSender;
import org.malenikajkat.exception.EmailSendingException;

import java.util.List;

public class EmailNotificationService implements NotificationService {

    private final EmailSender emailSender;

    public EmailNotificationService(EmailSender emailSender) {
        if (emailSender == null) {
            throw new IllegalArgumentException("EmailSender не может быть null");
        }
        this.emailSender = emailSender;
    }

    @Override
    public void sendNotification(User user, String title, String message) throws EmailSendingException {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        if (!supportsUser(user)) {
            throw new IllegalArgumentException(
                    "Некорректный email пользователя: " + user.getEmail()
            );
        }

        String subject = "[App Notification] " + title;
        String body = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Уведомление от системы:\n\n" +
                        "%s\n\n" +
                        "С уважением,\n" +
                        "Команда приложения",
                user.getLogin(),
                message
        );

        emailSender.send(user.getEmail(), subject, body);
    }

    @Override
    public boolean supportsUser(User user) {
        return user != null &&
                user.getEmail() != null &&
                !user.getEmail().trim().isEmpty() &&
                isValidEmail(user.getEmail());
    }

    @Override
    public void notifyUserDeleted(User user) {
        try {
            sendNotification(user, "Удаление аккаунта", "Ваш аккаунт был удалён из системы.");
        } catch (EmailSendingException e) {
            System.err.println("Ошибка отправки уведомления об удалении пользователя: " + e.getMessage());
        }
    }

    @Override
    public void notifyCategoryDeleted(User user, String categoryName) {
        try {
            sendNotification(user, "Удаление категории", "Категория '" + categoryName + "' была удалена из вашего профиля.");
        } catch (EmailSendingException e) {
            System.err.println("Ошибка отправки уведомления об удалении категории: " + e.getMessage());
        }
    }

    @Override
    public void notifyTransactionDeleted(User user, Transaction transaction) {
        try {
            sendNotification(user, "Удаление транзакции", "Транзакция '" + transaction.getDescription() + "' была удалена из вашего кошелька.");
        } catch (EmailSendingException e) {
            System.err.println("Ошибка отправки уведомления об удалении транзакции: " + e.getMessage());
        }
    }

    @Override
    public int getNotificationCount() {
        return 0;
    }

    @Override
    public String getLastNotificationMessage() {
        return "";
    }

    @Override
    public void clearNotifications() {
    }

    @Override
    public boolean hasRecentNotificationForUser(String username) {
        return false;
    }

    @Override
    public List<String> getAllNotificationMessages() {
        return List.of();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}