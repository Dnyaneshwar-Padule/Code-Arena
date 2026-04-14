package controller;

import java.io.IOException;
import java.util.List;

import exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PracticeLeaderboardEntry;
import model.User;
import service.PracticeLeaderboardService;
import service.impl.PracticeLeaderboardServiceImpl;
import util.ErrorHandlerUtil;

@WebServlet(name = "PracticeLeaderboardServlet", urlPatterns = "/leaderboard")
public class PracticeLeaderboardServlet extends HttpServlet {

    private transient PracticeLeaderboardService practiceLeaderboardService;

    @Override
    public void init() throws ServletException {
        this.practiceLeaderboardService = new PracticeLeaderboardServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<PracticeLeaderboardEntry> leaderboard = practiceLeaderboardService.getPracticeLeaderboard();
            PracticeLeaderboardEntry currentUserEntry = null;
            User loggedInUser = getLoggedInUser(request);
            if (loggedInUser != null) {
                for (int i = 0; i < leaderboard.size(); i++) {
                    PracticeLeaderboardEntry entry = leaderboard.get(i);
                    if (entry.getUserId() != null && entry.getUserId().equals(loggedInUser.getId())) {
                        currentUserEntry = entry;
                        leaderboard.remove(i);
                        break;
                    }
                }
            }
            request.setAttribute("currentUserEntry", currentUserEntry);
            request.setAttribute("leaderboardEntries", leaderboard);
            request.getRequestDispatcher("/jsp/leaderboard.jsp").forward(request, response);
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load leaderboard right now.",
                    "/jsp/leaderboard.jsp"
            );
        }
    }

    private User getLoggedInUser(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return null;
        }
        Object value = request.getSession(false).getAttribute("loggedInUser");
        if (!(value instanceof User)) {
            return null;
        }
        return (User) value;
    }
}
