package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Contest;
import model.ContestLeaderboardEntry;
import model.User;
import service.ContestService;
import service.impl.ContestServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ContestLeaderboardServlet", urlPatterns = "/leaderboard/contest")
public class ContestLeaderboardServlet extends HttpServlet {

    private transient ContestService contestService;

    @Override
    public void init() throws ServletException {
        this.contestService = new ContestServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long contestId = parseLong(request.getParameter("contestId"), "Invalid contest id.");
            Contest contest = contestService.getContestById(contestId);
            List<ContestLeaderboardEntry> entries = contestService.getLeaderboard(contestId);

            ContestLeaderboardEntry currentUserEntry = null;
            User loggedInUser = getLoggedInUser(request);
            if (loggedInUser != null && !entries.isEmpty()) {
                for (int i = 0; i < entries.size(); i++) {
                    ContestLeaderboardEntry entry = entries.get(i);
                    if (entry.getUserId() != null && entry.getUserId().equals(loggedInUser.getId())) {
                        currentUserEntry = entry;
                        entries.remove(i);
                        break;
                    }
                }
            }

            request.setAttribute("contest", contest);
            request.setAttribute("currentUserEntry", currentUserEntry);
            request.setAttribute("leaderboardEntries", entries);
            request.getRequestDispatcher("/jsp/contest-leaderboard.jsp").forward(request, response);
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load leaderboard right now.",
                    "/jsp/contest-list.jsp"
            );
        }
    }

    private Long parseLong(String rawValue, String errorMessage) {
        String value = rawValue == null ? null : rawValue.trim();
        if (value == null || value.isBlank()) {
            throw new ValidationException(errorMessage);
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new ValidationException(errorMessage);
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
