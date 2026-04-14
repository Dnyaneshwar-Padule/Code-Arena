package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Problem;
import model.Submission;
import model.User;
import service.ProblemService;
import service.SubmissionService;
import service.TestCaseService;
import service.impl.ProblemServiceImpl;
import service.impl.SubmissionServiceImpl;
import service.impl.TestCaseServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "SubmissionServlet", urlPatterns = {"/submit", "/submissions"})
public class SubmissionServlet extends HttpServlet {

    private transient SubmissionService submissionService;
    private transient ProblemService problemService;
    private transient TestCaseService testCaseService;

    @Override
    public void init() throws ServletException {
        this.submissionService = new SubmissionServiceImpl();
        this.problemService = new ProblemServiceImpl();
        this.testCaseService = new TestCaseServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = getLoggedInUser(request);
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long problemId = null;
        try {
            problemId = parseLong(getTrimmedParameter(request, "problemId"));
            String code = getTrimmedParameter(request, "code");
            String language = getTrimmedParameter(request, "language");

            Submission submission = submissionService.submit(loggedInUser.getId(), problemId, code, language);

            Problem problem = problemService.getProblemById(problemId);
            request.setAttribute("problem", problem);
            request.setAttribute("sampleTestCases", testCaseService.getSampleByProblemId(problemId));
            request.setAttribute("submissionStatus", submission.getStatus());
            request.setAttribute("submissionOutput", submission.getOutput());
            request.setAttribute("submissionError", submission.getErrorMessage());
            request.setAttribute("submissionExecutionTime", submission.getExecutionTime());
            request.setAttribute("submissions", submissionService.getUserSubmissions(problemId, loggedInUser.getId()));
            request.setAttribute("activeTab", "run");
            request.getRequestDispatcher("/jsp/problem-detail.jsp").forward(request, response);
        } catch (ServiceException ex) {
            if (problemId != null) {
                attachProblemContext(request, problemId);
            }
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to process submission right now.",
                    "/jsp/problem-detail.jsp"
            );
        } catch (Exception ex) {
            if (problemId != null) {
                attachProblemContext(request, problemId);
            }
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to process submission right now.",
                    "/jsp/problem-detail.jsp"
            );
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = getLoggedInUser(request);
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long problemId = null;
        try {
            problemId = parseLong(getTrimmedParameter(request, "problemId"));
            Problem problem = problemService.getProblemById(problemId);
            List<Submission> submissions = submissionService.getUserSubmissions(problemId, loggedInUser.getId());
            request.setAttribute("problem", problem);
            request.setAttribute("sampleTestCases", testCaseService.getSampleByProblemId(problemId));
            request.setAttribute("submissions", submissions);
            request.setAttribute("activeTab", "submissions");
            request.getRequestDispatcher("/jsp/problem-detail.jsp").forward(request, response);
        } catch (Exception ex) {
            if (problemId != null) {
                attachProblemContext(request, problemId);
            }
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load submission history right now.",
                    "/jsp/problem-detail.jsp"
            );
        }
    }

    private void attachProblemContext(HttpServletRequest request, Long problemId) {
        try {
            request.setAttribute("problem", problemService.getProblemById(problemId));
            request.setAttribute("sampleTestCases", testCaseService.getSampleByProblemId(problemId));
            User loggedInUser = getLoggedInUser(request);
            if (loggedInUser != null) {
                request.setAttribute("submissions", submissionService.getUserSubmissions(problemId, loggedInUser.getId()));
            }
        } catch (Exception ignored) {
            // Keep error path resilient if context lookup fails.
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

    private Long parseLong(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new ValidationException("Invalid problem id.");
        }
        try {
            return Long.valueOf(rawValue);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Invalid problem id.");
        }
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }
}
