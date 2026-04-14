package service.impl;

import dao.ProblemDAO;
import dao.SubmissionDAO;
import dao.UserDAO;
import dao.impl.ProblemDAOImpl;
import dao.impl.SubmissionDAOImpl;
import dao.impl.UserDAOImpl;
import exception.DaoException;
import exception.ServiceException;
import exception.ValidationException;
import judge.JudgeResult;
import model.Language;
import model.Problem;
import model.Submission;
import model.SubmissionStatus;
import model.User;
import service.JudgeService;
import service.SubmissionService;

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
    private final JudgeService judgeService;

    public SubmissionServiceImpl() {
        this.submissionDAO = new SubmissionDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.problemDAO = new ProblemDAOImpl();
        this.judgeService = new JudgeServiceImpl();
    }

    @Override
    public Submission submit(Long userId, Long problemId, String code, String language) {
        validateInput(userId, problemId, code, language);

        Submission savedSubmission = null;
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new ValidationException("User not found.");
            }

            Problem problem = problemDAO.getProblemById(problemId);
            if (problem == null) {
                throw new ValidationException("Problem not found.");
            }

            Submission submission = new Submission();
            submission.setUser(user);
            submission.setProblem(problem);
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

            return updateSubmissionWithJudgeResult(savedSubmission, judgeResult);
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
