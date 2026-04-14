package dao;

import model.UserProblemSolved;
import model.PracticeLeaderboardEntry;

import java.util.List;

public interface UserProblemSolvedDAO {

    boolean exists(Long userId, Long problemId);

    UserProblemSolved save(UserProblemSolved userProblemSolved);

    List<PracticeLeaderboardEntry> getLeaderboard();
}
