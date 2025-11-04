package org.malenikajkat.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SmtpEmailSender implements EmailSender {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean useSsl;

    public SmtpEmailSender(String host, int port, String username, String password, boolean useSsl) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.useSsl = useSsl;
    }

    @Override
    public void send(String to, String subject, String body) throws EmailSendingException {
        if (!isConfigured()) {
            throw new EmailSendingException("SMTP-отправитель не настроен");
        }
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Адрес получателя не может быть пустым");
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", String.valueOf(port));
            props.put("mail.smtp.auth", "true");
            if (useSsl) {
                props.put("mail.smtp.ssl.enable", "true");
            } else {
                props.put("mail.smtp.starttls.enable", "true");
            }

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new EmailSendingException("Ошибка при отправке email", e);
        }
    }

    @Override
    public boolean isConfigured() {
        return host != null && !host.trim().isEmpty() &&
                port > 0 &&
                username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty();
    }
}
