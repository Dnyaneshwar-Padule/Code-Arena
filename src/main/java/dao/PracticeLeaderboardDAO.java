package dao;

import model.PracticeLeaderboardEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface PracticeLeaderboardDAO {

    boolean awardPracticeSolve(Long userId, Long problemId, int points, LocalDateTime solvedAt);

    List<PracticeLeaderboardEntry> getPracticeLeaderboard();
}
