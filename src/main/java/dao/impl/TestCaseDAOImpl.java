package dao.impl;

import dao.TestCaseDAO;
import exception.DaoException;
import model.TestCase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hibernate-based implementation for test case data operations.
 */
public class TestCaseDAOImpl implements TestCaseDAO {

    private static final Logger LOGGER = Logger.getLogger(TestCaseDAOImpl.class.getName());

    @Override
    public TestCase createTestCase(TestCase testCase) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(testCase);
            transaction.commit();
            return testCase;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error creating test case", ex);
            throw new DaoException("Error creating test case", ex);
        }
    }

    @Override
    public TestCase getTestCaseById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TestCase.class, id);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching test case by id", ex);
            throw new DaoException("Error fetching test case by id", ex);
        }
    }

    @Override
    public List<TestCase> getAllByProblemId(Long problemId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from TestCase tc where tc.problem.id = :problemId order by tc.id asc",
                            TestCase.class
                    )
                    .setParameter("problemId", problemId)
                    .list();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching all test cases by problem id", ex);
            throw new DaoException("Error fetching all test cases by problem id", ex);
        }
    }

    @Override
    public List<TestCase> getSampleByProblemId(Long problemId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from TestCase tc where tc.problem.id = :problemId and tc.sample = true order by tc.id asc",
                            TestCase.class
                    )
                    .setParameter("problemId", problemId)
                    .list();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching sample test cases by problem id", ex);
            throw new DaoException("Error fetching sample test cases by problem id", ex);
        }
    }

    @Override
    public TestCase updateTestCase(TestCase testCase) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            TestCase merged = (TestCase) session.merge(testCase);
            transaction.commit();
            return merged;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating test case", ex);
            throw new DaoException("Error updating test case", ex);
        }
    }

    @Override
    public void deleteTestCase(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            TestCase testCase = session.get(TestCase.class, id);
            if (testCase != null) {
                session.remove(testCase);
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error deleting test case", ex);
            throw new DaoException("Error deleting test case", ex);
        }
    }
}
