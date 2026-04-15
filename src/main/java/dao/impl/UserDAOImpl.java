package dao.impl;

import dao.UserDAO;
import exception.DaoException;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hibernate-based implementation of UserDAO.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());

    @Override
    public void saveUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error saving user", ex);
            throw new DaoException("Error saving user", ex);
        }
    }

    @Override
    public void updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating user", ex);
            throw new DaoException("Error updating user", ex);
        }
    }

    @Override
    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error finding user by id", ex);
            throw new DaoException("Error finding user", ex);
        }
    }

    @Override
    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error finding user by email", ex);
            throw new DaoException("Error finding user", ex);
        }
    }

    @Override
    public User findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User u where u.username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error finding user by username", ex);
            throw new DaoException("Error finding user", ex);
        }
    }
}
