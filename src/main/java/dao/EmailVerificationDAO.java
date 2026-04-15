package dao;

import model.EmailVerification;
import model.EmailVerificationPurpose;

import java.time.LocalDateTime;

public interface EmailVerificationDAO {

    EmailVerification save(EmailVerification verification);

    EmailVerification findById(Long id);

    void update(EmailVerification verification);

    void invalidateActiveByEmailAndPurpose(String email, EmailVerificationPurpose purpose, LocalDateTime now);

    long countByEmailPurposeCreatedAfter(String email, EmailVerificationPurpose purpose, LocalDateTime after);
}
