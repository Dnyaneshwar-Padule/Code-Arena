package dao.impl;

import dao.PracticeLeaderboardDAO;
import exception.DaoException;
import model.PracticeLeaderboardEntry;
import model.UserProblemSolved;
import model.UserProblemSolvedId;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PracticeLeaderboardDAOImpl implements PracticeLeaderboardDAO {

    private static final Logger LOGGER = Logger.getLogger(PracticeLeaderboardDAOImpl.class.getName());

    @Override
    public boolean awardPracticeSolve(Long userId, Long problemId, int points, LocalDateTime solvedAt) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            UserProblemSolvedId key = new UserProblemSolvedId(userId, problemId);
            UserProblemSolved existing = session.get(UserProblemSolved.class, key);
            if (existing != null) {
                transaction.commit();
                return false;
            }

            UserProblemSolved solved = new UserProblemSolved();
            solved.setId(key);
            solved.setUser(session.getReference(model.User.class, userId));
            solved.setProblem(session.getReference(model.Problem.class, problemId));
            solved.setPointsAwarded(Math.max(0, points));
            solved.setSolvedAt(solvedAt == null ? LocalDateTime.now() : solvedAt);
            session.persist(solved);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error awarding practice solve points", ex);
            throw new DaoException("Error awarding practice solve points", ex);
        }
    }

    @Override
    public List<PracticeLeaderboardEntry> getPracticeLeaderboard() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            @SuppressWarnings("unchecked")
            List<Object[]> rows = session.createNativeQuery(
                            "select "
                                    + "row_number() over (order by sum(ups.points_awarded) desc, u.username asc) as rank_position, "
                                    + "u.id as user_id, "
                                    + "u.username as username, "
                                    + "sum(ups.points_awarded) as total_points "
                                    + "from user_problem_solved ups "
                                    + "join users u on u.id = ups.user_id "
                                    + "group by u.id, u.username "
                                    + "order by total_points desc, u.username asc"
                    )
                    .list();
            List<PracticeLeaderboardEntry> entries = new ArrayList<>();
            for (Object[] row : rows) {
                entries.add(new PracticeLeaderboardEntry(
                        row[1] == null ? null : ((Number) row[1]).longValue(),
                        row[2] == null ? "" : String.valueOf(row[2]),
                        row[3] == null ? 0 : ((Number) row[3]).intValue(),
                        row[0] == null ? 0 : ((Number) row[0]).intValue()
                ));
            }
            return entries;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching practice leaderboard", ex);
            throw new DaoException("Error fetching practice leaderboard", ex);
        }
    }
}
