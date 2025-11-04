package org.malenikajkat.service.notification;

import org.malenikajkat.model.Transaction;
import org.malenikajkat.model.User;
import org.malenikajkat.exception.EmailSendingException;

import java.util.List;

public interface NotificationService {
    void sendNotification(User user, String title, String message) throws EmailSendingException;
    boolean supportsUser(User user);
    void notifyUserDeleted(User user);
    void notifyCategoryDeleted(User user, String categoryName);
    void notifyTransactionDeleted(User user, Transaction transaction);
    int getNotificationCount();
    String getLastNotificationMessage();
    void clearNotifications();
    boolean hasRecentNotificationForUser(String username);
    List<String> getAllNotificationMessages();
}