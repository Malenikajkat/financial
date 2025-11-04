package org.malenikajkat.service.notification;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendNotification(User user, String title, String message) {
        System.out.printf("Отправлено уведомление пользователю '%s': заголовок='%s', сообщение='%s'%n", user.getLogin(), title, message);
    }

    @Override
    public boolean supportsUser(User user) {
        return true;
    }

    @Override
    public void notifyUserDeleted(User user) {
        System.out.println("Пользователь " + user.getLogin() + " удалён.");
    }

    @Override
    public void notifyCategoryDeleted(User user, String categoryName) {
        System.out.println("Категория '" + categoryName + "' удалена для пользователя " + user.getLogin());
    }

    @Override
    public void notifyTransactionDeleted(User user, Transaction transaction) {
        System.out.println("Транзакция (" + transaction.getDescription() + ") удалена для пользователя " + user.getLogin());
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