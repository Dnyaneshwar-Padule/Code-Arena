package service;

import model.EmailVerificationPurpose;

public interface EmailVerificationService {

    Long createVerification(String email, EmailVerificationPurpose purpose);

    Long resendVerification(String email, EmailVerificationPurpose purpose);

    void verifyOtp(Long verificationId, String otp, EmailVerificationPurpose purpose);
}
