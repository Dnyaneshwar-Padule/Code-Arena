package service;

import java.util.List;
import java.util.Map;

import model.Contest;
import model.ContestLeaderboardEntry;
import model.ContestProblem;
import model.ContestProblemProgressStatus;
import model.ContestState;

public interface ContestService {

    List<Contest> getAllContests();

    Contest getContestById(Long contestId);

    List<ContestProblem> getContestProblems(Long contestId);

    ContestState getContestState(Contest contest);

    boolean isProblemInContest(Long contestId, Long problemId);

    List<ContestLeaderboardEntry> getLeaderboard(Long contestId);

    Map<Long, ContestProblemProgressStatus> getUserProblemStatuses(Long contestId, Long userId);

    void applyAcceptedSubmissionScore(Long submissionId);

    Contest createContest(String title, String description, String startTime, String endTime);

    Contest updateContest(Long contestId, String title, String description, String startTime, String endTime);

    void deleteContest(Long contestId);

    ContestProblem addProblemToContest(Long contestId, Long problemId, Integer order, Integer points);
}
