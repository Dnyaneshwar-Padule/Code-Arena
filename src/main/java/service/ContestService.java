package service;

import model.Contest;
import model.ContestLeaderboardEntry;
import model.ContestProblem;
import model.ContestState;

import java.util.List;

public interface ContestService {

    List<Contest> getAllContests();

    Contest getContestById(Long contestId);

    List<ContestProblem> getContestProblems(Long contestId);

    ContestState getContestState(Contest contest);

    boolean isProblemInContest(Long contestId, Long problemId);

    List<ContestLeaderboardEntry> getLeaderboard(Long contestId);

    void applyAcceptedSubmissionScore(Long submissionId);

    Contest createContest(String title, String description, String startTime, String endTime);

    Contest updateContest(Long contestId, String title, String description, String startTime, String endTime);

    void deleteContest(Long contestId);

    ContestProblem addProblemToContest(Long contestId, Long problemId, Integer order, Integer points);
}
