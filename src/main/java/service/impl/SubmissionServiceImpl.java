package service.impl;

import dao.ProblemDAO;
import dao.SubmissionDAO;
import dao.UserProblemSolvedDAO;
import dao.UserDAO;
import dao.impl.ProblemDAOImpl;
import dao.impl.SubmissionDAOImpl;
import dao.impl.UserProblemSolvedDAOImpl;
import dao.impl.UserDAOImpl;
import exception.DaoException;
import exception.ServiceException;
import exception.ValidationException;
import judge.JudgeResult;
import model.Contest;
import model.Language;
import model.Problem;
import model.Submission;
import model.SubmissionStatus;
import model.User;
import model.UserProblemSolved;
import model.UserProblemSolvedId;
import service.ContestService;
import service.JudgeService;
import service.SubmissionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Hibernate;

/**
 * Default implementation for submission and judging workflow.
 */
public class SubmissionServiceImpl implements SubmissionService {

    private static final Logger LOGGER = Logger.getLogger(SubmissionServiceImpl.class.getName());

    private final SubmissionDAO submissionDAO;
    private final UserDAO userDAO;
    private final ProblemDAO problemDAO;
    private final UserProblemSolvedDAO userProblemSolvedDAO;
    private final JudgeService judgeService;
    private final ContestService contestService;

    public SubmissionServiceImpl() {
        this.submissionDAO = new SubmissionDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.problemDAO = new ProblemDAOImpl();
        this.userProblemSolvedDAO = new UserProblemSolvedDAOImpl();
        this.judgeService = new JudgeServiceImpl();
        this.contestService = new ContestServiceImpl();
    }

    @Override
    public Submission submit(Long userId, Long problemId, String code, String language) {
        return submit(userId, problemId, code, language, null);
    }

    @Override
    public Submission submit(Long userId, Long problemId, String code, String language, Long contestId) {
        validateInput(userId, problemId, code, language);

        Submission savedSubmission = null;
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new ValidationException("User not found.");
            }

            Problem problem = problemDAO.getProblemById(problemId, true);
            if (problem == null) {
                throw new ValidationException("Problem not found.");
            }

            //Hibernate.initialize(problem);
            
            Contest contest = resolveContestIfPresent(contestId, problemId);

            Submission submission = new Submission();
            submission.setUser(user);
            submission.setProblem(problem);
            submission.setContest(contest);
            submission.setCode(code.trim());
            submission.setLanguage(Language.fromValue(language).name());
            submission.setStatus(SubmissionStatus.PENDING);
            submission.setExecutionTime(0);
            submission.setPassedCount(0);
            submission.setTotalCount(0);

            savedSubmission = submissionDAO.createSubmission(submission);
            
            JudgeResult judgeResult = judgeService.judge(
                    problemId,
                    submission.getCode(),
                    submission.getLanguage()
            );

            Submission updatedSubmission = updateSubmissionWithJudgeResult(savedSubmission, judgeResult);
            if (updatedSubmission.getContest() != null && updatedSubmission.getStatus() == SubmissionStatus.ACCEPTED) {
                contestService.applyAcceptedSubmissionScore(updatedSubmission.getId());
            } else if (updatedSubmission.getContest() == null && updatedSubmission.getStatus() == SubmissionStatus.ACCEPTED) {
                awardPracticePointsIfFirstAccepted(user, problem);
            }
            return updatedSubmission;
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to save submission", ex);
            throw new ServiceException("Unable to process submission right now.", ex);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unsupported language.");
        } catch (Exception ex) {
            markSubmissionAsError(savedSubmission);
            LOGGER.log(Level.SEVERE, "Unexpected submission workflow error", ex);
            throw new ServiceException("Unable to process submission right now.", ex);
        }
    }

    @Override
    public List<Submission> getUserSubmissions(Long problemId, Long userId) {
        if (problemId == null || problemId <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user id.");
        }
        try {
            return submissionDAO.getByUserAndProblem(userId, problemId);
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch user submissions", ex);
            throw new ServiceException("Unable to load submissions right now.", ex);
        }
    }

    @Override
    public Submission getUserSubmissionById(Long submissionId, Long userId) {
        if (submissionId == null || submissionId <= 0) {
            throw new ValidationException("Invalid submission id.");
        }
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user id.");
        }
        try {
            Submission submission = submissionDAO.getSubmissionById(submissionId);
            if (submission == null) {
                throw new ValidationException("Submission not found.");
            }
            if (submission.getUser() == null
                    || submission.getUser().getId() == null
                    || !submission.getUser().getId().equals(userId)) {
                throw new ValidationException("Submission not found.");
            }
            return submission;
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch submission details", ex);
            throw new ServiceException("Unable to load submission details right now.", ex);
        }
    }

    private void validateInput(Long userId, Long problemId, String code, String language) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user id.");
        }
        if (problemId == null || problemId <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Code is required.");
        }
        if (language == null || language.trim().isEmpty()) {
            throw new ValidationException("Language is required.");
        }
    }

    private Integer toInteger(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    private Submission updateSubmissionWithJudgeResult(Submission savedSubmission, JudgeResult judgeResult) {
        savedSubmission.setStatus(judgeResult.getFinalStatus());
        savedSubmission.setOutput(judgeResult.getOutput());
        savedSubmission.setErrorMessage(judgeResult.getErrorMessage());
        savedSubmission.setExecutionTime(toInteger(judgeResult.getExecutionTime()));
        savedSubmission.setPassedCount(judgeResult.getPassedCount());
        savedSubmission.setTotalCount(judgeResult.getTotalCount());
        return submissionDAO.updateSubmission(savedSubmission);
    }

    private Contest resolveContestIfPresent(Long contestId, Long problemId) {
        if (contestId == null) {
            return null;
        }
        if (contestId <= 0) {
            throw new ValidationException("Invalid contest id.");
        }
        Contest contest = contestService.getContestById(contestId);
        if (!contestService.isProblemInContest(contestId, problemId)) {
            throw new ValidationException("Problem is not part of this contest.");
        }
        return contest;
    }

    private void awardPracticePointsIfFirstAccepted(User user, Problem problem) {
        Long userId = user.getId();
        Long problemId = problem.getId();
        boolean alreadySolved = userProblemSolvedDAO.exists(userId, problemId);
        if (alreadySolved) {
            return;
        }
        Integer points = problem.getPoints();
        int safePoints = points == null ? 0 : Math.max(0, points);
        UserProblemSolved solved = new UserProblemSolved();
        solved.setId(new UserProblemSolvedId(userId, problemId));
        solved.setUser(user);
        solved.setProblem(problem);   // problem is detached !
        solved.setPointsAwarded(safePoints);
        solved.setSolvedAt(LocalDateTime.now());
        userProblemSolvedDAO.save(solved);
        LOGGER.info("Awarded " + safePoints + " points to user " + userId + " for problem " + problemId);
        boolean debugExists = userProblemSolvedDAO.exists(userId, problemId);
        LOGGER.info("Debug: user_problem_solved row present for user " + userId + ", problem " + problemId + " = " + debugExists);
    }

    private void markSubmissionAsError(Submission savedSubmission) {
        if (savedSubmission == null) {
            return;
        }
        try {
            savedSubmission.setStatus(SubmissionStatus.ERROR);
            savedSubmission.setErrorMessage("Judge execution failed.");
            savedSubmission.setExecutionTime(0);
            savedSubmission.setPassedCount(0);
            if (savedSubmission.getTotalCount() == null) {
                savedSubmission.setTotalCount(0);
            }
            submissionDAO.updateSubmission(savedSubmission);
        } catch (Exception updateFailure) {
            LOGGER.log(Level.SEVERE, "Failed to persist ERROR status for submission " + savedSubmission.getId(), updateFailure);
        }
    }
}
