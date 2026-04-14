package service.impl;

import dao.ContestDAO;
import dao.impl.ContestDAOImpl;
import exception.ServiceException;
import exception.ValidationException;
import model.Contest;
import model.ContestLeaderboardEntry;
import model.ContestProblem;
import model.ContestState;
import model.Submission;
import model.SubmissionStatus;
import service.ContestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContestServiceImpl implements ContestService {

    private static final Logger LOGGER = Logger.getLogger(ContestServiceImpl.class.getName());
    private final ContestDAO contestDAO;

    public ContestServiceImpl() {
        this.contestDAO = new ContestDAOImpl();
    }

    @Override
    public List<Contest> getAllContests() {
        try {
            return contestDAO.getAllContests();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch contests", ex);
            throw new ServiceException("Unable to load contests right now.", ex);
        }
    }

    @Override
    public Contest getContestById(Long contestId) {
        validateContestId(contestId);
        try {
            Contest contest = contestDAO.getContestById(contestId);
            if (contest == null) {
                throw new ValidationException("Contest not found.");
            }
            return contest;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch contest", ex);
            throw new ServiceException("Unable to load contest right now.", ex);
        }
    }

    @Override
    public List<ContestProblem> getContestProblems(Long contestId) {
        validateContestId(contestId);
        try {
            return contestDAO.getContestProblems(contestId);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch contest problems", ex);
            throw new ServiceException("Unable to load contest problems right now.", ex);
        }
    }

    @Override
    public ContestState getContestState(Contest contest) {
        if (contest == null || contest.getStartTime() == null || contest.getEndTime() == null) {
            throw new ValidationException("Contest schedule is invalid.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(contest.getStartTime())) {
            return ContestState.NOT_STARTED;
        }
        if (now.isAfter(contest.getEndTime())) {
            return ContestState.ENDED;
        }
        return ContestState.RUNNING;
    }

    @Override
    public boolean isProblemInContest(Long contestId, Long problemId) {
        validateContestId(contestId);
        if (problemId == null || problemId <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        try {
            return contestDAO.isProblemInContest(contestId, problemId);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to validate contest problem relation", ex);
            throw new ServiceException("Unable to validate contest problem right now.", ex);
        }
    }

    @Override
    public List<ContestLeaderboardEntry> getLeaderboard(Long contestId) {
        validateContestId(contestId);
        try {
            return contestDAO.getLeaderboard(contestId);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch leaderboard", ex);
            throw new ServiceException("Unable to load leaderboard right now.", ex);
        }
    }

    @Override
    public void applyAcceptedSubmissionScore(Long submissionId) {
        if (submissionId == null || submissionId <= 0) {
            throw new ValidationException("Invalid submission id.");
        }
        try {
            Submission submission = contestDAO.getSubmissionById(submissionId);
            if (submission == null
                    || submission.getContest() == null
                    || submission.getUser() == null
                    || submission.getProblem() == null
                    || submission.getStatus() != SubmissionStatus.ACCEPTED) {
                return;
            }
            Long contestId = submission.getContest().getId();
            Long userId = submission.getUser().getId();
            Long problemId = submission.getProblem().getId();
            boolean alreadySolved = contestDAO.hasAcceptedSubmission(contestId, userId, problemId, submissionId);
            if (alreadySolved) {
                return;
            }
            int points = contestDAO.getProblemPoints(contestId, problemId);
            contestDAO.upsertLeaderboardScore(contestId, userId, points);
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to apply contest scoring", ex);
            throw new ServiceException("Unable to apply contest scoring right now.", ex);
        }
    }

    @Override
    public Contest createContest(String title, String description, String startTime, String endTime) {
        LocalDateTime parsedStart = parseDateTime(startTime, "Invalid start time.");
        LocalDateTime parsedEnd = parseDateTime(endTime, "Invalid end time.");
        validateContestFields(title, parsedStart, parsedEnd);
        Contest contest = new Contest();
        contest.setTitle(title.trim());
        contest.setDescription(description == null ? null : description.trim());
        contest.setStartTime(parsedStart);
        contest.setEndTime(parsedEnd);
        try {
            return contestDAO.createContest(contest);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to create contest", ex);
            throw new ServiceException("Unable to create contest right now.", ex);
        }
    }

    @Override
    public Contest updateContest(Long contestId, String title, String description, String startTime, String endTime) {
        validateContestId(contestId);
        LocalDateTime parsedStart = parseDateTime(startTime, "Invalid start time.");
        LocalDateTime parsedEnd = parseDateTime(endTime, "Invalid end time.");
        validateContestFields(title, parsedStart, parsedEnd);
        Contest existing = getContestById(contestId);
        existing.setTitle(title.trim());
        existing.setDescription(description == null ? null : description.trim());
        existing.setStartTime(parsedStart);
        existing.setEndTime(parsedEnd);
        try {
            return contestDAO.updateContest(existing);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to update contest", ex);
            throw new ServiceException("Unable to update contest right now.", ex);
        }
    }

    @Override
    public void deleteContest(Long contestId) {
        validateContestId(contestId);
        try {
            contestDAO.deleteContest(contestId);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete contest", ex);
            throw new ServiceException("Unable to delete contest right now.", ex);
        }
    }

    @Override
    public ContestProblem addProblemToContest(Long contestId, Long problemId, Integer order, Integer points) {
        validateContestId(contestId);
        if (problemId == null || problemId <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        if (order == null || order <= 0) {
            throw new ValidationException("Order must be positive.");
        }
        if (points == null || points <= 0) {
            throw new ValidationException("Points must be positive.");
        }
        if (contestDAO.contestProblemExists(contestId, problemId)) {
            throw new ValidationException("Problem already exists in contest.");
        }
        getContestById(contestId);
        try {
            return contestDAO.addContestProblem(contestId, problemId, order, points);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to add problem to contest", ex);
            throw new ServiceException("Unable to add problem to contest right now.", ex);
        }
    }

    private void validateContestId(Long contestId) {
        if (contestId == null || contestId <= 0) {
            throw new ValidationException("Invalid contest id.");
        }
    }

    private void validateContestFields(String title, LocalDateTime start, LocalDateTime end) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Title is required.");
        }
        if (!end.isAfter(start)) {
            throw new ValidationException("End time must be after start time.");
        }
    }

    private LocalDateTime parseDateTime(String raw, String message) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new ValidationException(message);
        }
        try {
            return LocalDateTime.parse(raw.trim());
        } catch (DateTimeParseException ex) {
            throw new ValidationException(message);
        }
    }
}
