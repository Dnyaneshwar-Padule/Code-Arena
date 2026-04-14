package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Problem;
import model.User;
import service.TestCaseService;
import service.SubmissionService;
import service.ProblemService;
import service.impl.ProblemServiceImpl;
import service.impl.SubmissionServiceImpl;
import service.impl.TestCaseServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProblemServlet", urlPatterns = {"/problems", "/problem"})
public class ProblemServlet extends HttpServlet {

    private transient ProblemService problemService;
    private transient TestCaseService testCaseService;
    private transient SubmissionService submissionService;

    @Override
    public void init() throws ServletException {
        this.problemService = new ProblemServiceImpl();
        this.testCaseService = new TestCaseServiceImpl();
        this.submissionService = new SubmissionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if ("/problem".equals(servletPath)) {
            showProblemDetail(request, response);
            return;
        }
        listProblems(request, response);
    }

    private void listProblems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = parsePositiveIntOrDefault(request.getParameter("page"), 1);
        int size = parsePositiveIntOrDefault(request.getParameter("size"), 12);
        try {
            long totalCount = problemService.getTotalProblemCount();
            long totalPages = problemService.getTotalPages(size);
            if (totalPages > 0 && page > totalPages) {
                page = (int) totalPages;
            }

            List<Problem> problems = problemService.getProblems(page, size);
            request.setAttribute("problems", problems);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("size", size);
            request.getRequestDispatcher("/jsp/problems.jsp").forward(request, response);
        } catch (ValidationException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Invalid pagination request.",
                    "/jsp/problems.jsp"
            );
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load problems right now.",
                    "/jsp/problems.jsp"
            );
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load problems right now.",
                    "/jsp/problems.jsp"
            );
        }
    }

    private void showProblemDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long id = parseLong(request.getParameter("id"));
            Problem problem = problemService.getProblemById(id);
            request.setAttribute("problem", problem);
            request.setAttribute("sampleTestCases", testCaseService.getSampleByProblemId(id));
            request.setAttribute("contestId", parseLong(request.getParameter("contestId")));
            User loggedInUser = getLoggedInUser(request);
            if (loggedInUser != null) {
                request.setAttribute("submissions", submissionService.getUserSubmissions(id, loggedInUser.getId()));
            }
            request.getRequestDispatcher("/jsp/problem-detail.jsp").forward(request, response);
        } catch (ValidationException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Invalid problem request.",
                    "/jsp/problems.jsp"
            );
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load problem details.",
                    "/jsp/problems.jsp"
            );
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load problem details.",
                    "/jsp/problems.jsp"
            );
        }
    }

    private Long parseLong(String rawValue) {
        String trimmedValue = rawValue == null ? null : rawValue.trim();
        if (trimmedValue == null || trimmedValue.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(trimmedValue);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Invalid problem id.");
        }
    }

    private int parsePositiveIntOrDefault(String rawValue, int defaultValue) {
        String trimmedValue = rawValue == null ? null : rawValue.trim();
        if (trimmedValue == null || trimmedValue.isBlank()) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(trimmedValue);
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException ex) {
            return defaultValue;
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
