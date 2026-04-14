package service.impl;

import dao.UserProblemSolvedDAO;
import dao.impl.UserProblemSolvedDAOImpl;
import exception.ServiceException;
import model.PracticeLeaderboardEntry;
import service.PracticeLeaderboardService;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PracticeLeaderboardServiceImpl implements PracticeLeaderboardService {

    private static final Logger LOGGER = Logger.getLogger(PracticeLeaderboardServiceImpl.class.getName());
    private final UserProblemSolvedDAO userProblemSolvedDAO;

    public PracticeLeaderboardServiceImpl() {
        this.userProblemSolvedDAO = new UserProblemSolvedDAOImpl();
    }

    @Override
    public List<PracticeLeaderboardEntry> getPracticeLeaderboard() {
        try {
            return userProblemSolvedDAO.getLeaderboard();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch practice leaderboard", ex);
            throw new ServiceException("Unable to load leaderboard right now.", ex);
        }
    }
}
