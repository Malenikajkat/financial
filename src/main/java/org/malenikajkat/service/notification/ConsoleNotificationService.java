package org.malenikajkat.service.notification;

import org.malenikajkat.model.User;
import java.util.List;

public class ConsoleNotificationService implements NotificationService {

    @Override
    public void sendNotification(User user, String title, String message) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }

        System.out.println("=== NOTIFICATION ===");
        System.out.println("To: " + user.getLogin());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Title: " + title);
        System.out.println("Message: " + message);
        System.out.println("==================");
    }

    @Override
    public boolean supportsUser(User user) {
        return user != null;
    }

    @Override
    public void notifyUserDeleted(User user) {
        sendNotification(user, "Удаление аккаунта", "Ваш аккаунт был удалён из системы.");
    }

    @Override
    public void notifyCategoryDeleted(User user, String categoryName) {
        sendNotification(user, "Удаление категории", "Категория '" + categoryName + "' была удалена из вашего профиля.");
    }

    @Override
    public void notifyTransactionDeleted(User user, org.malenikajkat.model.Transaction transaction) {
        sendNotification(user, "Удаление транзакции", "Транзакция '" + transaction.getDescription() + "' была удалена из вашего кошелька.");
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
}