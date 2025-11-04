package org.malenikajkat.service.notification;

import org.malenikajkat.model.User;
import org.malenikajkat.util.EmailSender;

public class EmailNotificationService implements NotificationService {

    private final EmailSender emailSender;

    public EmailNotificationService(EmailSender emailSender) {
        if (emailSender == null) {
            throw new IllegalArgumentException("EmailSender не может быть null");
        }
        this.emailSender = emailSender;
    }

    @Override
    public void sendNotification(User user, String title, String message) {
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
                ("Здравствуйте, %s!\n\n" +
                        "Уведомление от системы:\n\n" +
                        "%s\n\n" +
                        "С уважением,\n" +
                        "Команда приложения"),
                user.getLogin(), message
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

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
