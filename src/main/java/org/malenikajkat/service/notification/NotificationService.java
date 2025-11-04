package org.malenikajkat.service.notification;

import org.malenikajkat.model.User;

public interface NotificationService {

    void sendNotification(User user, String title, String message);

    boolean supportsUser(User user);
}
