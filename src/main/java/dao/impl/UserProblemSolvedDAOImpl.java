package dao.impl;

import dao.UserProblemSolvedDAO;
import exception.DaoException;
import model.PracticeLeaderboardEntry;
import model.Problem;
import model.User;
import model.UserProblemSolved;
import model.UserProblemSolvedId;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserProblemSolvedDAOImpl implements UserProblemSolvedDAO {

    private static final Logger LOGGER = Logger.getLogger(UserProblemSolvedDAOImpl.class.getName());

    @Override
    public boolean exists(Long userId, Long problemId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(UserProblemSolved.class, new UserProblemSolvedId(userId, problemId)) != null;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error checking user problem solved existence", ex);
            throw new DaoException("Error checking user problem solved existence", ex);
        }
    }

    @Override
//    public UserProblemSolved save(UserProblemSolved userProblemSolved) {
//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//            session.persist(userProblemSolved);
//            transaction.commit();
//            return userProblemSolved;
//        } catch (Exception ex) {
//        	ex.printStackTrace();
//        	System.out.println("UserProblemSolvedDAOImpl.save()");
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            LOGGER.log(Level.SEVERE, "Error saving user problem solved", ex);
//            throw new DaoException("Error saving user problem solved", ex);
//        }
//    }


    public UserProblemSolved save(UserProblemSolved userProblemSolved) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User managedUser = session.getReference(
                    User.class,
                    userProblemSolved.getUser().getId()
            );

            Problem managedProblem = session.getReference(
                    Problem.class,
                    userProblemSolved.getProblem().getId()
            );

            userProblemSolved.setUser(managedUser);
            userProblemSolved.setProblem(managedProblem);

            session.persist(userProblemSolved);

            transaction.commit();
            return userProblemSolved;

        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException("Error saving user problem solved", ex);
        }
    }
    
    @Override
    public List<PracticeLeaderboardEntry> getLeaderboard() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            @SuppressWarnings("unchecked")
            List<Object[]> rows = session.createNativeQuery(
                    "SELECT "
                            + "u.id, "
                            + "u.username, "
                            + "COALESCE(SUM(ups.points_awarded), 0) AS total_points, "
                            + "COUNT(ups.problem_id) AS solved_count, "
                            + "ROW_NUMBER() OVER ( "
                            + "    ORDER BY COALESCE(SUM(ups.points_awarded),0) DESC, "
                            + "             COUNT(ups.problem_id) DESC, "
                            + "             u.username ASC "
                            + ") AS rank "
                            + "FROM users u "
                            + "LEFT JOIN user_problem_solved ups ON u.id = ups.user_id "
                            + "GROUP BY u.id, u.username "
                            + "ORDER BY total_points DESC, solved_count DESC, u.username ASC"
            ).list();
            List<PracticeLeaderboardEntry> entries = new ArrayList<>();
            for (Object[] row : rows) {
                entries.add(new PracticeLeaderboardEntry(
                        row[0] == null ? null : ((Number) row[0]).longValue(),
                        row[1] == null ? "" : String.valueOf(row[1]),
                        row[2] == null ? 0 : ((Number) row[2]).intValue(),
                        row[3] == null ? 0 : ((Number) row[3]).intValue(),
                        row[4] == null ? 0 : ((Number) row[4]).intValue()
                ));
            }
            return entries;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching practice leaderboard", ex);
            throw new DaoException("Error fetching practice leaderboard", ex);
        }
    }
}
