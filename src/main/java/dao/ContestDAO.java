package dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import model.Contest;
import model.ContestLeaderboardEntry;
import model.ContestProblem;
import model.ContestProblemProgressStatus;
import model.Submission;

public interface ContestDAO {

    List<Contest> getAllContests();

    Contest getContestById(Long contestId);

    List<ContestProblem> getContestProblems(Long contestId);

    boolean isProblemInContest(Long contestId, Long problemId);

    Integer getProblemPoints(Long contestId, Long problemId);

    List<ContestLeaderboardEntry> getLeaderboard(Long contestId);

    boolean hasAcceptedSubmission(Long contestId, Long userId, Long problemId, Long excludeSubmissionId);

    Map<Long, ContestProblemProgressStatus> getUserProblemStatuses(Long contestId, Long userId);

    boolean awardFirstSolveAndUpdateLeaderboard(Long contestId, Long userId, Long problemId, int points, LocalDateTime solvedAt);

    void upsertLeaderboardScore(Long contestId, Long userId, int points);

    Submission getSubmissionById(Long submissionId);

    Contest createContest(Contest contest);

    Contest updateContest(Contest contest);

    void deleteContest(Long contestId);

    boolean contestProblemExists(Long contestId, Long problemId);

    void shiftProblemOrderFrom(Long contestId, int orderFrom);

    ContestProblem addContestProblem(Long contestId, Long problemId, int order, int points);
}
