package service;

import model.EmailVerificationPurpose;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp, EmailVerificationPurpose purpose);
}
