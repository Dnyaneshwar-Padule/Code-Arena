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
import model.ContestProblem;
import model.ContestState;
import service.ContestService;
import service.impl.ContestServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "ContestServlet", urlPatterns = {"/contests", "/contest", "/contest/leaderboard"})
public class ContestServlet extends HttpServlet {

    private transient ContestService contestService;

    @Override
    public void init() throws ServletException {
        this.contestService = new ContestServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/contests".equals(path)) {
            showContestList(request, response);
            return;
        }
        if ("/contest/leaderboard".equals(path)) {
            showLeaderboard(request, response);
            return;
        }
        showContestDetail(request, response);
    }

    private void showContestList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Contest> contests = contestService.getAllContests();
            request.setAttribute("contests", contests);
            request.getRequestDispatcher("/jsp/contest-list.jsp").forward(request, response);
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load contests right now.",
                    "/jsp/contest-list.jsp"
            );
        }
    }

    private void showContestDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long contestId = parseLong(request.getParameter("id"), "Invalid contest id.");
            Contest contest = contestService.getContestById(contestId);
            List<ContestProblem> contestProblems = contestService.getContestProblems(contestId);
            ContestState state = contestService.getContestState(contest);
            long remainingSeconds = computeRemainingSeconds(contest, state);

            request.setAttribute("contest", contest);
            request.setAttribute("contestProblems", contestProblems);
            request.setAttribute("contestState", state);
            request.setAttribute("remainingSeconds", remainingSeconds);
            request.getRequestDispatcher("/jsp/contest-detail.jsp").forward(request, response);
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load contest details right now.",
                    "/jsp/contest-list.jsp"
            );
        }
    }

    private void showLeaderboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long contestId = parseLong(request.getParameter("contestId"), "Invalid contest id.");
            Contest contest = contestService.getContestById(contestId);
            List<ContestLeaderboardEntry> entries = contestService.getLeaderboard(contestId);

            request.setAttribute("contest", contest);
            request.setAttribute("leaderboardEntries", entries);
            request.getRequestDispatcher("/jsp/contest-leaderboard.jsp").forward(request, response);
        } catch (ValidationException | ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load leaderboard right now.",
                    "/jsp/contest-list.jsp"
            );
        } catch (Exception ex) {
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

    private long computeRemainingSeconds(Contest contest, ContestState state) {
        if (state == ContestState.NOT_STARTED) {
            return Math.max(0, Duration.between(LocalDateTime.now(), contest.getStartTime()).getSeconds());
        }
        if (state == ContestState.RUNNING) {
            return Math.max(0, Duration.between(LocalDateTime.now(), contest.getEndTime()).getSeconds());
        }
        return 0;
    }
}
