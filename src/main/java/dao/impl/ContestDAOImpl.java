package dao.impl;

import dao.ContestDAO;
import exception.DaoException;
import model.Contest;
import model.ContestLeaderboardEntry;
import model.ContestProblem;
import model.Leaderboard;
import model.Submission;
import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            List<Leaderboard> rows = session.createQuery(
                            "from Leaderboard l where l.contest.id = :contestId order by l.score desc, l.user.username asc",
                            Leaderboard.class
                    )
                    .setParameter("contestId", contestId)
                    .list();
            List<ContestLeaderboardEntry> entries = new ArrayList<>();
            int rank = 1;
            for (Leaderboard row : rows) {
                ContestLeaderboardEntry entry = new ContestLeaderboardEntry(
                        row.getUser().getId(),
                        row.getUser().getUsername(),
                        row.getScore() == null ? 0 : row.getScore()
                );
                entry.setRank(rank++);
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
                int current = row.getScore() == null ? 0 : row.getScore();
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
