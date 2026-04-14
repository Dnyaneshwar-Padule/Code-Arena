package dao.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import dao.ContestDAO;
import exception.DaoException;
import model.Contest;
import model.ContestLeaderboardEntry;
import model.ContestProblem;
import model.ContestProblemProgressStatus;
import model.Leaderboard;
import model.Submission;
import model.User;
import model.UserContestProblem;
import model.UserContestProblemId;
import util.HibernateUtil;

public class ContestDAOImpl implements ContestDAO {

    private static final Logger LOGGER = Logger.getLogger(ContestDAOImpl.class.getName());

    @Override
    public List<Contest> getAllContests() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Contest c order by c.startTime desc", Contest.class).list();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching contests", ex);
            throw new DaoException("Error fetching contests", ex);
        }
    }

    @Override
    public Contest getContestById(Long contestId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Contest.class, contestId);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching contest", ex);
            throw new DaoException("Error fetching contest", ex);
        }
    }

    @Override
    public List<ContestProblem> getContestProblems(Long contestId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from ContestProblem cp where cp.contest.id = :contestId order by cp.order asc, cp.id asc",
                            ContestProblem.class
                    )
                    .setParameter("contestId", contestId)
                    .list();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching contest problems", ex);
            throw new DaoException("Error fetching contest problems", ex);
        }
    }

    @Override
    public boolean isProblemInContest(Long contestId, Long problemId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(cp.id) from ContestProblem cp where cp.contest.id = :contestId and cp.problem.id = :problemId",
                            Long.class
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("problemId", problemId)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error validating contest problem relation", ex);
            throw new DaoException("Error validating contest problem relation", ex);
        }
    }

    @Override
    public Integer getProblemPoints(Long contestId, Long problemId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer points = session.createQuery(
                            "select cp.points from ContestProblem cp where cp.contest.id = :contestId and cp.problem.id = :problemId",
                            Integer.class
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("problemId", problemId)
                    .uniqueResult();
            return points == null ? 0 : points;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching contest problem points", ex);
            throw new DaoException("Error fetching contest problem points", ex);
        }
    }

    @Override
    public List<ContestLeaderboardEntry> getLeaderboard(Long contestId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            @SuppressWarnings("unchecked")
            List<Object[]> rows = session.createNativeQuery(
                            "select "
                                    + "row_number() over (order by l.score desc, u.username asc) as rank_position, "
                                    + "u.id as user_id, "
                                    + "u.username as username, "
                                    + "l.score as score "
                                    + "from leaderboard l "
                                    + "join users u on u.id = l.user_id "
                                    + "where l.contest_id = :contestId "
                                    + "order by l.score desc, u.username asc"
                    )
                    .setParameter("contestId", contestId)
                    .list();
            List<ContestLeaderboardEntry> entries = new ArrayList<>();
            for (Object[] row : rows) {
                Integer rankValue = row[0] == null ? 0 : ((Number) row[0]).intValue();
                Long userId = row[1] == null ? null : ((Number) row[1]).longValue();
                String username = row[2] == null ? "" : String.valueOf(row[2]);
                Integer scoreValue = row[3] == null ? 0 : ((Number) row[3]).intValue();
                ContestLeaderboardEntry entry = new ContestLeaderboardEntry(userId, username, scoreValue);
                entry.setRank(rankValue);
                entries.add(entry);
            }
            return entries;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching leaderboard", ex);
            throw new DaoException("Error fetching leaderboard", ex);
        }
    }

    @Override
    public boolean hasAcceptedSubmission(Long contestId, Long userId, Long problemId, Long excludeSubmissionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "select count(s.id) from Submission s "
                    + "where s.contest.id = :contestId and s.user.id = :userId and s.problem.id = :problemId and s.status = :acceptedStatus";
            if (excludeSubmissionId != null) {
                hql += " and s.id <> :excludeSubmissionId";
            }
            Query<Long> query = session.createQuery(hql, Long.class)
                    .setParameter("contestId", contestId)
                    .setParameter("userId", userId)
                    .setParameter("problemId", problemId)
                    .setParameter("acceptedStatus", model.SubmissionStatus.ACCEPTED);
            if (excludeSubmissionId != null) {
                query.setParameter("excludeSubmissionId", excludeSubmissionId);
            }
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error checking accepted contest submission", ex);
            throw new DaoException("Error checking accepted contest submission", ex);
        }
    }

    @Override
    public Map<Long, ContestProblemProgressStatus> getUserProblemStatuses(Long contestId, Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Long> problemIds = session.createQuery(
                            "select cp.problem.id from ContestProblem cp where cp.contest.id = :contestId",
                            Long.class
                    )
                    .setParameter("contestId", contestId)
                    .list();

            Map<Long, ContestProblemProgressStatus> statuses = new HashMap<>();
            for (Long problemId : problemIds) {
                statuses.put(problemId, ContestProblemProgressStatus.NOT_ATTEMPTED);
            }

            if (problemIds.isEmpty()) {
                return statuses;
            }

            List<Long> solvedProblemIds = session.createQuery(
                            "select ucp.problem.id from UserContestProblem ucp "
                                    + "where ucp.contest.id = :contestId and ucp.user.id = :userId",
                            Long.class
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("userId", userId)
                    .list();

            Set<Long> solvedSet = new HashSet<>(solvedProblemIds);
            for (Long solvedProblemId : solvedSet) {
                statuses.put(solvedProblemId, ContestProblemProgressStatus.SOLVED);
            }

            List<Long> attemptedProblemIds = session.createQuery(
                            "select distinct s.problem.id from Submission s "
                                    + "where s.contest.id = :contestId and s.user.id = :userId",
                            Long.class
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("userId", userId)
                    .list();

            for (Long attemptedProblemId : attemptedProblemIds) {
                if (!solvedSet.contains(attemptedProblemId)) {
                    statuses.put(attemptedProblemId, ContestProblemProgressStatus.ATTEMPTED);
                }
            }
            return statuses;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching contest problem statuses", ex);
            throw new DaoException("Error fetching contest problem statuses", ex);
        }
    }

    @Override
    public boolean awardFirstSolveAndUpdateLeaderboard(Long contestId, Long userId, Long problemId, int points, LocalDateTime solvedAt) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            UserContestProblemId key = new UserContestProblemId(userId, contestId, problemId);
            UserContestProblem existing = session.get(UserContestProblem.class, key);
            if (existing != null) {
                transaction.commit();
                return false;
            }

            UserContestProblem solved = new UserContestProblem();
            solved.setId(key);
            solved.setUser(session.getReference(User.class, userId));
            solved.setContest(session.getReference(Contest.class, contestId));
            solved.setProblem(session.getReference(model.Problem.class, problemId));
            solved.setPointsAwarded(Math.max(0, points));
            solved.setSolvedAt(solvedAt == null ? LocalDateTime.now() : solvedAt);
            session.persist(solved);

            Leaderboard row = session.createQuery(
                            "from Leaderboard l where l.contest.id = :contestId and l.user.id = :userId",
                            Leaderboard.class
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("userId", userId)
                    .uniqueResult();

            int safePoints = Math.max(0, points);
            if (row == null) {
                row = new Leaderboard();
                row.setContest(session.getReference(Contest.class, contestId));
                row.setUser(session.getReference(User.class, userId));
                row.setScore(safePoints);
                session.persist(row);
            } else {
                Integer currentScore = row.getScore();
                int current = currentScore == null ? 0 : currentScore;
                row.setScore(Math.max(0, current + safePoints));
                session.merge(row);
            }

            transaction.commit();
            return true;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error awarding contest first-solve score", ex);
            throw new DaoException("Error awarding contest first-solve score", ex);
        }
    }

    @Override
    public void upsertLeaderboardScore(Long contestId, Long userId, int points) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Leaderboard row = session.createQuery(
                            "from Leaderboard l where l.contest.id = :contestId and l.user.id = :userId",
                            Leaderboard.class
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("userId", userId)
                    .uniqueResult();
            if (row == null) {
                row = new Leaderboard();
                Contest contestRef = session.getReference(Contest.class, contestId);
                User userRef = session.getReference(User.class, userId);
                row.setContest(contestRef);
                row.setUser(userRef);
                row.setScore(Math.max(0, points));
                session.persist(row);
            } else {
                Integer currentScore = row.getScore();
                int current = currentScore == null ? 0 : currentScore;
                row.setScore(Math.max(0, current + points));
                session.merge(row);
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error upserting leaderboard score", ex);
            throw new DaoException("Error upserting leaderboard score", ex);
        }
    }

    @Override
    public Submission getSubmissionById(Long submissionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Submission.class, submissionId);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching submission for contest scoring", ex);
            throw new DaoException("Error fetching submission for contest scoring", ex);
        }
    }

    @Override
    public Contest createContest(Contest contest) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(contest);
            transaction.commit();
            return contest;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error creating contest", ex);
            throw new DaoException("Error creating contest", ex);
        }
    }

    @Override
    public Contest updateContest(Contest contest) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Contest merged = (Contest) session.merge(contest);
            transaction.commit();
            return merged;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating contest", ex);
            throw new DaoException("Error updating contest", ex);
        }
    }

    @Override
    public void deleteContest(Long contestId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery("delete from ContestProblem cp where cp.contest.id = :contestId")
                    .setParameter("contestId", contestId)
                    .executeUpdate();
            session.createMutationQuery("delete from UserContestProblem ucp where ucp.contest.id = :contestId")
                .setParameter("contestId", contestId)
                .executeUpdate();
            session.createMutationQuery("delete from Leaderboard l where l.contest.id = :contestId")
                    .setParameter("contestId", contestId)
                    .executeUpdate();
            session.createMutationQuery("delete from ContestRegistration cr where cr.contest.id = :contestId")
                    .setParameter("contestId", contestId)
                    .executeUpdate();
            session.createMutationQuery("update Submission s set s.contest = null where s.contest.id = :contestId")
                    .setParameter("contestId", contestId)
                    .executeUpdate();
            session.createMutationQuery("delete from Contest c where c.id = :contestId")
                    .setParameter("contestId", contestId)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error deleting contest", ex);
            throw new DaoException("Error deleting contest", ex);
        }
    }

    @Override
    public boolean contestProblemExists(Long contestId, Long problemId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(cp.id) from ContestProblem cp where cp.contest.id = :contestId and cp.problem.id = :problemId",
                            Long.class
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("problemId", problemId)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error checking contest problem duplicate", ex);
            throw new DaoException("Error checking contest problem duplicate", ex);
        }
    }

    @Override
    public void shiftProblemOrderFrom(Long contestId, int orderFrom) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery(
                            "update ContestProblem cp set cp.order = cp.order + 1 where cp.contest.id = :contestId and cp.order >= :orderFrom"
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("orderFrom", orderFrom)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error shifting contest problem order", ex);
            throw new DaoException("Error shifting contest problem order", ex);
        }
    }

    @Override
    public ContestProblem addContestProblem(Long contestId, Long problemId, int order, int points) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery(
                            "update ContestProblem cp set cp.order = cp.order + 1 where cp.contest.id = :contestId and cp.order >= :orderFrom"
                    )
                    .setParameter("contestId", contestId)
                    .setParameter("orderFrom", order)
                    .executeUpdate();
            ContestProblem contestProblem = new ContestProblem();
            contestProblem.setContest(session.getReference(Contest.class, contestId));
            contestProblem.setProblem(session.getReference(model.Problem.class, problemId));
            contestProblem.setOrder(order);
            contestProblem.setPoints(points);
            session.persist(contestProblem);
            transaction.commit();
            return contestProblem;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error adding problem to contest", ex);
            throw new DaoException("Error adding problem to contest", ex);
        }
    }
}
