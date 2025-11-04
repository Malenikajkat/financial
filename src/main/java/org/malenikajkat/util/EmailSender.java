package org.malenikajkat.util;

import org.malenikajkat.exception.EmailSendingException;

public interface EmailSender {

    void send(String to, String subject, String body) throws EmailSendingException;

    boolean isConfigured();
}
