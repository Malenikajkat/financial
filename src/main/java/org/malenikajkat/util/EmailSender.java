package org.malenikajkat.util;

public interface EmailSender {

    void send(String to, String subject, String body) throws EmailSendingException;

    boolean isConfigured();
}
