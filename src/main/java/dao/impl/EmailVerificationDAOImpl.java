package dao.impl;

import dao.EmailVerificationDAO;
import exception.DaoException;
import model.EmailVerification;
import model.EmailVerificationPurpose;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailVerificationDAOImpl implements EmailVerificationDAO {

    private static final Logger LOGGER = Logger.getLogger(EmailVerificationDAOImpl.class.getName());

    @Override
    public EmailVerification save(EmailVerification verification) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(verification);
            transaction.commit();
            return verification;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error saving email verification", ex);
            throw new DaoException("Error saving email verification", ex);
        }
    }

    @Override
    public EmailVerification findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(EmailVerification.class, id);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error finding email verification by id", ex);
            throw new DaoException("Error finding email verification", ex);
        }
    }

    @Override
    public void update(EmailVerification verification) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(verification);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating email verification", ex);
            throw new DaoException("Error updating email verification", ex);
        }
    }

    @Override
    public void invalidateActiveByEmailAndPurpose(String email, EmailVerificationPurpose purpose, LocalDateTime now) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery(
                            "update EmailVerification e " +
                            "set e.expiresAt = :now " +
                            "where e.email = :email and e.purpose = :purpose and e.verified = false and e.expiresAt > :now"
                    )
                    .setParameter("now", now)
                    .setParameter("email", email)
                    .setParameter("purpose", purpose)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error invalidating active OTP verifications", ex);
            throw new DaoException("Error invalidating active OTP verifications", ex);
        }
    }

    @Override
    public long countByEmailPurposeCreatedAfter(String email, EmailVerificationPurpose purpose, LocalDateTime after) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(e.id) from EmailVerification e " +
                            "where e.email = :email and e.purpose = :purpose and e.createdAt >= :after", Long.class
                    )
                    .setParameter("email", email)
                    .setParameter("purpose", purpose)
                    .setParameter("after", after)
                    .uniqueResult();
            return count == null ? 0L : count;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error counting recent OTP verifications", ex);
            throw new DaoException("Error counting recent OTP verifications", ex);
        }
    }
}
