package dao.impl;

import dao.SubmissionDAO;
import exception.DaoException;
import model.Submission;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hibernate-based implementation for submission data operations.
 */
public class SubmissionDAOImpl implements SubmissionDAO {

    private static final Logger LOGGER = Logger.getLogger(SubmissionDAOImpl.class.getName());

    @Override
    public Submission createSubmission(Submission submission) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(submission);
            transaction.commit();
            return submission;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error creating submission", ex);
            throw new DaoException("Error creating submission", ex);
        }
    }

    @Override
    public Submission updateSubmission(Submission submission) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Submission merged = (Submission) session.merge(submission);
            transaction.commit();
            return merged;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating submission", ex);
            throw new DaoException("Error updating submission", ex);
        }
    }

    @Override
    public Submission getSubmissionById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Submission.class, id);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching submission by id", ex);
            throw new DaoException("Error fetching submission by id", ex);
        }
    }
}
