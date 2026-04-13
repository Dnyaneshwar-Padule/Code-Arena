package dao.impl;

import dao.ProblemDAO;
import exception.DaoException;
import model.Problem;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hibernate-based implementation for problem data operations.
 */
public class ProblemDAOImpl implements ProblemDAO {

    private static final Logger LOGGER = Logger.getLogger(ProblemDAOImpl.class.getName());

    @Override
    public List<Problem> getAllProblems() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Problem p order by p.createdAt desc", Problem.class).list();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching problems", ex);
            throw new DaoException("Error fetching problems", ex);
        }
    }

    @Override
    public List<Problem> getProblems(int page, int size) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Problem p order by p.createdAt desc", Problem.class)
                    .setFirstResult((page - 1) * size)
                    .setMaxResults(size)
                    .list();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching paginated problems", ex);
            throw new DaoException("Error fetching paginated problems", ex);
        }
    }

    @Override
    public long getTotalProblemCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("select count(p.id) from Problem p", Long.class).uniqueResult();
            return count == null ? 0L : count;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching problem count", ex);
            throw new DaoException("Error fetching problem count", ex);
        }
    }

    @Override
    public Problem getProblemById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Problem.class, id);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching problem by id", ex);
            throw new DaoException("Error fetching problem", ex);
        }
    }

    @Override
    public Problem createProblem(Problem problem) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(problem);
            transaction.commit();
            return problem;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error creating problem", ex);
            throw new DaoException("Error creating problem", ex);
        }
    }

    @Override
    public Problem updateProblem(Problem problem) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Problem merged = (Problem) session.merge(problem);
            transaction.commit();
            return merged;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating problem", ex);
            throw new DaoException("Error updating problem", ex);
        }
    }

    @Override
    public void deleteProblem(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Problem problem = session.get(Problem.class, id);
            if (problem != null) {
                session.remove(problem);
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error deleting problem", ex);
            throw new DaoException("Error deleting problem", ex);
        }
    }
}
