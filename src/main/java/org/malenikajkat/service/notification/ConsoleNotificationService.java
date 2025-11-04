package org.malenikajkat.service.notification;

import org.malenikajkat.model.User;

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
}
