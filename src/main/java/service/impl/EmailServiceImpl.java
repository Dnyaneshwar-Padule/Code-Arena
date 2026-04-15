package service.impl;

import exception.ServiceException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import model.EmailVerificationPurpose;
import service.EmailService;

import java.io.InputStream;
import java.util.Properties;

public class EmailServiceImpl implements EmailService {

    private static final String PRIMARY_CONFIG_PATH = "mail-api.properties";
    private static final String FALLBACK_CONFIG_PATH = "mail-api.peoperties";

    private final Properties config;

    public EmailServiceImpl() {
        this.config = loadMailProperties();
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp, EmailVerificationPurpose purpose) {
        Properties smtpProps = new Properties();
        smtpProps.put("mail.smtp.host", getRequired("mail.smtp.host"));
        smtpProps.put("mail.smtp.port", getRequired("mail.smtp.port"));
        smtpProps.put("mail.smtp.auth", config.getProperty("mail.smtp.auth", "true"));
        smtpProps.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable", "true"));

        final String smtpUsername = getRequired("mail.smtp.username");
        final String smtpPassword = getRequired("mail.smtp.password");
        final String fromEmail = getRequired("mail.from");

        Session session = Session.getInstance(smtpProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subjectFor(purpose));
            message.setText(bodyFor(otp, purpose));
            Transport.send(message);
        } catch (MessagingException ex) {
            throw new ServiceException("Unable to send OTP email right now. Please try again.", ex);
        }
    }

    private String subjectFor(EmailVerificationPurpose purpose) {
        if (purpose == EmailVerificationPurpose.FORGOT_PASSWORD) {
            return "Code Arena Password Reset OTP";
        }
        return "Code Arena Registration OTP";
    }

    private String bodyFor(String otp, EmailVerificationPurpose purpose) {
        String useCase = purpose == EmailVerificationPurpose.FORGOT_PASSWORD
                ? "reset your password"
                : "verify your registration";
        return "Your OTP is: " + otp + "\n\n" +
                "Use this OTP to " + useCase + ".\n" +
                "This OTP will expire in 5 minutes.\n" +
                "If you did not request this, you can ignore this email.";
    }

    private Properties loadMailProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(PRIMARY_CONFIG_PATH)) {
            if (inputStream != null) {
                properties.load(inputStream);
                return properties;
            }
        } catch (Exception ex) {
            throw new ServiceException("Unable to load mail configuration.", ex);
        }

        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(FALLBACK_CONFIG_PATH)) {
            if (inputStream != null) {
                properties.load(inputStream);
                return properties;
            }
        } catch (Exception ex) {
            throw new ServiceException("Unable to load mail configuration.", ex);
        }

        throw new ServiceException("Mail configuration file not found.");
    }

    private String getRequired(String key) {
        String value = config.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new ServiceException("Missing mail configuration: " + key);
        }
        return value.trim();
    }
}
