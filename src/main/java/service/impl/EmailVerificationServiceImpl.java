package service.impl;

import dao.EmailVerificationDAO;
import dao.impl.EmailVerificationDAOImpl;
import exception.DaoException;
import exception.ServiceException;
import exception.ValidationException;
import model.EmailVerification;
import model.EmailVerificationPurpose;
import service.EmailService;
import service.EmailVerificationService;
import util.OtpUtil;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final Logger LOGGER = Logger.getLogger(EmailVerificationServiceImpl.class.getName());

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;

    private final EmailVerificationDAO emailVerificationDAO;
    private final EmailService emailService;

    public EmailVerificationServiceImpl() {
        this(new EmailVerificationDAOImpl(), new EmailServiceImpl());
    }

    public EmailVerificationServiceImpl(EmailVerificationDAO emailVerificationDAO, EmailService emailService) {
        this.emailVerificationDAO = emailVerificationDAO;
        this.emailService = emailService;
    }

    @Override
    public Long createVerification(String email, EmailVerificationPurpose purpose) {
        try {
            String normalizedEmail = normalizeAndValidateEmail(email);
            LocalDateTime now = LocalDateTime.now();
            enforceResendCooldown(normalizedEmail, purpose, now);

            emailVerificationDAO.invalidateActiveByEmailAndPurpose(normalizedEmail, purpose, now);

            String otp = OtpUtil.generateSixDigitOtp();
            EmailVerification verification = new EmailVerification();
            verification.setEmail(normalizedEmail);
            verification.setOtpHash(OtpUtil.hashOtp(otp));
            verification.setPurpose(purpose);
            verification.setExpiresAt(now.plusMinutes(OTP_EXPIRY_MINUTES));
            verification.setAttemptCount(0);
            verification.setVerified(Boolean.FALSE);

            EmailVerification saved = emailVerificationDAO.save(verification);
            emailService.sendOtpEmail(normalizedEmail, otp, purpose);
            return saved.getId();
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Create verification failed", ex);
            throw new ServiceException("Unable to process OTP request right now.", ex);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected create verification error", ex);
            throw new ServiceException("Unable to process OTP request right now.", ex);
        }
    }

    @Override
    public Long resendVerification(String email, EmailVerificationPurpose purpose) {
        return createVerification(email, purpose);
    }

    @Override
    public void verifyOtp(Long verificationId, String otp, EmailVerificationPurpose purpose) {
        try {
            if (verificationId == null) {
                throw new ValidationException("OTP session is invalid. Please request a new OTP.");
            }
            String normalizedOtp = otp == null ? "" : otp.trim();
            if (!normalizedOtp.matches("^\\d{6}$")) {
                throw new ValidationException("Please enter a valid 6-digit OTP.");
            }

            EmailVerification verification = emailVerificationDAO.findById(verificationId);
            LocalDateTime now = LocalDateTime.now();

            if (verification == null || verification.getPurpose() != purpose) {
                throw new ValidationException("Invalid OTP verification request.");
            }
            if (Boolean.TRUE.equals(verification.getVerified())) {
                throw new ValidationException("OTP is already used. Please request a new OTP.");
            }
            if (verification.getExpiresAt() == null || !verification.getExpiresAt().isAfter(now)) {
                throw new ValidationException("OTP is expired. Please request a new OTP.");
            }

            Integer currentAttemptCount = verification.getAttemptCount();
            int attempts = currentAttemptCount == null ? 0 : currentAttemptCount.intValue();
            if (attempts >= MAX_ATTEMPTS) {
                verification.setExpiresAt(now);
                emailVerificationDAO.update(verification);
                throw new ValidationException("Maximum OTP attempts exceeded. Please request a new OTP.");
            }

            if (!OtpUtil.matches(normalizedOtp, verification.getOtpHash())) {
                verification.setAttemptCount(attempts + 1);
                if (verification.getAttemptCount() >= MAX_ATTEMPTS) {
                    verification.setExpiresAt(now);
                }
                emailVerificationDAO.update(verification);
                throw new ValidationException("Invalid OTP. Please try again.");
            }

            verification.setVerified(Boolean.TRUE);
            emailVerificationDAO.update(verification);
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Verify OTP failed", ex);
            throw new ServiceException("Unable to verify OTP right now.", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected verify OTP error", ex);
            throw new ServiceException("Unable to verify OTP right now.", ex);
        }
    }

    private void enforceResendCooldown(String email, EmailVerificationPurpose purpose, LocalDateTime now) {
        long recentCount = emailVerificationDAO.countByEmailPurposeCreatedAfter(
                email,
                purpose,
                now.minusSeconds(RESEND_COOLDOWN_SECONDS)
        );
        if (recentCount > 0) {
            throw new ValidationException("Please wait before requesting another OTP.");
        }
    }

    private String normalizeAndValidateEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        if (normalized.isEmpty()) {
            throw new ValidationException("Email is required.");
        }
        if (!normalized.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidationException("Please enter a valid email address.");
        }
        return normalized;
    }
}
