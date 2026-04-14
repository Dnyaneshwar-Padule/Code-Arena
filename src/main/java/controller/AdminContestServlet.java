package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Contest;
import model.ContestProblem;
import model.Problem;
import model.User;
import model.UserRole;
import service.ContestService;
import service.ProblemService;
import service.impl.ContestServiceImpl;
import service.impl.ProblemServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "AdminContestServlet",
        urlPatterns = {
                "/admin/contests",
                "/admin/contest/create",
                "/admin/contest/edit",
                "/admin/contest/update",
                "/admin/contest/delete",
                "/admin/contest/problems",
                "/admin/contest/problems/add"
        }
)
public class AdminContestServlet extends HttpServlet {

    private transient ContestService contestService;
    private transient ProblemService problemService;

    @Override
    public void init() throws ServletException {
        this.contestService = new ContestServiceImpl();
        this.problemService = new ProblemServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }
        String path = request.getServletPath();
        switch (path) {
            case "/admin/contests" -> showContestList(request, response);
            case "/admin/contest/create" -> showCreateForm(request, response);
            case "/admin/contest/edit" -> showEditForm(request, response);
            case "/admin/contest/delete" -> deleteContest(request, response);
            case "/admin/contest/problems" -> showContestProblemsPage(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }
        String path = request.getServletPath();
        switch (path) {
            case "/admin/contest/create" -> createContest(request, response);
            case "/admin/contest/update" -> updateContest(request, response);
            case "/admin/contest/problems/add" -> addProblemToContest(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showContestList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("contests", contestService.getAllContests());
            request.getRequestDispatcher("/jsp/admin/admin-contest-list.jsp").forward(request, response);
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(
                    request, response, ex,
                    "Unable to load contests right now.",
                    "/jsp/admin/admin-contest-list.jsp"
            );
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("formMode", "create");
        request.getRequestDispatcher("/jsp/admin/admin-contest-create.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long contestId = parseLong(request.getParameter("id"), "Invalid contest id.");
            request.setAttribute("contest", contestService.getContestById(contestId));
            request.setAttribute("formMode", "edit");
            request.getRequestDispatcher("/jsp/admin/admin-contest-create.jsp").forward(request, response);
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(
                    request, response, ex,
                    "Unable to load contest for editing.",
                    "/jsp/admin/admin-contest-list.jsp"
            );
        }
    }

    private void createContest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            contestService.createContest(
                    getTrimmedParameter(request, "title"),
                    getTrimmedParameter(request, "description"),
                    getTrimmedParameter(request, "startTime"),
                    getTrimmedParameter(request, "endTime")
            );
            response.sendRedirect(request.getContextPath() + "/admin/contests");
        } catch (ValidationException | ServiceException ex) {
            request.setAttribute("formMode", "create");
            ErrorHandlerUtil.handleException(
                    request, response, ex,
                    "Unable to create contest.",
                    "/jsp/admin/admin-contest-create.jsp"
            );
        }
    }

    private void updateContest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long contestId = parseLong(request.getParameter("id"), "Invalid contest id.");
            contestService.updateContest(
                    contestId,
                    getTrimmedParameter(request, "title"),
                    getTrimmedParameter(request, "description"),
                    getTrimmedParameter(request, "startTime"),
                    getTrimmedParameter(request, "endTime")
            );
            response.sendRedirect(request.getContextPath() + "/admin/contests");
        } catch (ValidationException | ServiceException ex) {
            request.setAttribute("formMode", "edit");
            request.setAttribute("contest", buildContestFromRequest(request));
            ErrorHandlerUtil.handleException(
                    request, response, ex,
                    "Unable to update contest.",
                    "/jsp/admin/admin-contest-create.jsp"
            );
        }
    }

    private void deleteContest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long contestId = parseLong(request.getParameter("id"), "Invalid contest id.");
            contestService.deleteContest(contestId);
        } catch (Exception ignored) {
            // Keep delete flow simple.
        }
        response.sendRedirect(request.getContextPath() + "/admin/contests");
    }

    private void showContestProblemsPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long contestId = parseLong(request.getParameter("contestId"), "Invalid contest id.");
            request.setAttribute("contest", contestService.getContestById(contestId));
            request.setAttribute("contestProblems", contestService.getContestProblems(contestId));
            request.setAttribute("problems", problemService.getAllProblems());
            request.getRequestDispatcher("/jsp/admin/admin-contest-problems.jsp").forward(request, response);
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(
                    request, response, ex,
                    "Unable to load contest problems.",
                    "/jsp/admin/admin-contest-list.jsp"
            );
        }
    }

    private void addProblemToContest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long contestId = null;
        try {
            contestId = parseLong(getTrimmedParameter(request, "contestId"), "Invalid contest id.");
            Long problemId = parseLong(getTrimmedParameter(request, "problemId"), "Invalid problem id.");
            Integer order = parseInteger(getTrimmedParameter(request, "order"), "Invalid order.");
            Integer points = parseInteger(getTrimmedParameter(request, "points"), "Invalid points.");
            contestService.addProblemToContest(contestId, problemId, order, points);
            response.sendRedirect(request.getContextPath() + "/admin/contest/problems?contestId=" + contestId);
        } catch (ValidationException | ServiceException ex) {
            if (contestId != null) {
                try {
                    request.setAttribute("contest", contestService.getContestById(contestId));
                    request.setAttribute("contestProblems", contestService.getContestProblems(contestId));
                    request.setAttribute("problems", problemService.getAllProblems());
                } catch (Exception ignored) {
                    // Keep primary error.
                }
            }
            ErrorHandlerUtil.handleException(
                    request, response, ex,
                    "Unable to add problem to contest.",
                    "/jsp/admin/admin-contest-problems.jsp"
            );
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        Object loggedIn = request.getSession(false) == null ? null : request.getSession(false).getAttribute("loggedInUser");
        if (!(loggedIn instanceof User)) {
            return false;
        }
        return ((User) loggedIn).getRole() == UserRole.ADMIN;
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }

    private Long parseLong(String raw, String error) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new ValidationException(error);
        }
        try {
            return Long.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException(error);
        }
    }

    private Integer parseInteger(String raw, String error) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new ValidationException(error);
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException(error);
        }
    }

    private Contest buildContestFromRequest(HttpServletRequest request) {
        Contest contest = new Contest();
        String id = getTrimmedParameter(request, "id");
        if (id != null && !id.isBlank()) {
            try {
                contest.setId(Long.valueOf(id));
            } catch (NumberFormatException ignored) {
                // best effort only
            }
        }
        contest.setTitle(getTrimmedParameter(request, "title"));
        contest.setDescription(getTrimmedParameter(request, "description"));
        return contest;
    }
}
