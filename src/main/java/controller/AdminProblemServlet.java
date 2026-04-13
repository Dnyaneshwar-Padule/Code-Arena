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
import model.UserRole;
import service.ProblemService;
import service.TestCaseService;
import service.impl.ProblemServiceImpl;
import service.impl.TestCaseServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "AdminProblemServlet",
        urlPatterns = {
                "/admin/problems",
                "/admin/problem/create",
                "/admin/problem",
                "/admin/problem/edit",
                "/admin/problem/update",
                "/admin/problem/delete",
                "/admin/problem/testcase"
        }
)
public class AdminProblemServlet extends HttpServlet {

    private transient ProblemService problemService;
    private transient TestCaseService testCaseService;

    @Override
    public void init() throws ServletException {
        this.problemService = new ProblemServiceImpl();
        this.testCaseService = new TestCaseServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }

        String path = request.getServletPath();
        switch (path) {
            case "/admin/problems" -> showAdminProblems(request, response);
            case "/admin/problem/create" -> {
                request.setAttribute("formMode", "create");
                request.getRequestDispatcher("/jsp/admin/problem-form.jsp").forward(request, response);
            }
            case "/admin/problem/edit" -> showEditForm(request, response);
            case "/admin/problem/delete" -> deleteProblem(request, response);
            case "/admin/problem/testcase" -> showTestCaseForm(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }

        String path = request.getServletPath();
        switch (path) {
            case "/admin/problem" -> createProblem(request, response);
            case "/admin/problem/update" -> updateProblem(request, response);
            case "/admin/problem/testcase" -> addTestCase(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showAdminProblems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Problem> problems = problemService.getAllProblems();
            request.setAttribute("problems", problems);
            request.getRequestDispatcher("/jsp/admin/problems.jsp").forward(request, response);
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load admin problems.",
                    "/jsp/admin/problems.jsp"
            );
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long id = parseLong(request.getParameter("id"));
            Problem problem = problemService.getProblemById(id);
            request.setAttribute("problem", problem);
            request.setAttribute("formMode", "edit");
            request.getRequestDispatcher("/jsp/admin/problem-form.jsp").forward(request, response);
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load problem for editing.",
                    "/jsp/admin/problems.jsp"
            );
        }
    }

    private void createProblem(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            problemService.createProblem(
                    request.getParameter("title"),
                    request.getParameter("description"),
                    request.getParameter("difficulty"),
                    parseInteger(request.getParameter("timeLimit")),
                    parseInteger(request.getParameter("memoryLimit"))
            );
            response.sendRedirect(request.getContextPath() + "/admin/problems");
        } catch (ServiceException ex) {
            request.setAttribute("formMode", "create");
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to create problem.",
                    "/jsp/admin/problem-form.jsp"
            );
        }
    }

    private void updateProblem(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            Long id = parseLong(request.getParameter("id"));
            problemService.updateProblem(
                    id,
                    request.getParameter("title"),
                    request.getParameter("description"),
                    request.getParameter("difficulty"),
                    parseInteger(request.getParameter("timeLimit")),
                    parseInteger(request.getParameter("memoryLimit"))
            );
            response.sendRedirect(request.getContextPath() + "/admin/problems");
        } catch (ServiceException ex) {
            request.setAttribute("formMode", "edit");
            request.setAttribute("problem", buildProblemFromRequest(request));
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to update problem.",
                    "/jsp/admin/problem-form.jsp"
            );
        }
    }

    private void deleteProblem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = parseLong(request.getParameter("id"));
            problemService.deleteProblem(id);
        } catch (ServiceException ex) {
            // Keep delete flow simple: redirect back with graceful no-op on failure.
        }
        response.sendRedirect(request.getContextPath() + "/admin/problems");
    }

    private void showTestCaseForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long problemId = parseLong(request.getParameter("problemId"));
            Problem problem = problemService.getProblemById(problemId);
            request.setAttribute("problem", problem);
            request.getRequestDispatcher("/jsp/admin/test-case-form.jsp").forward(request, response);
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load test case form.",
                    "/jsp/admin/problems.jsp"
            );
        }
    }

    private void addTestCase(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            Long problemId = parseLong(request.getParameter("problemId"));
            boolean isSample = "on".equalsIgnoreCase(request.getParameter("isSample"));
            testCaseService.addTestCase(
                    problemId,
                    request.getParameter("input"),
                    request.getParameter("expectedOutput"),
                    isSample
            );
            response.sendRedirect(request.getContextPath() + "/admin/problem/edit?id=" + problemId);
        } catch (ServiceException ex) {
            Long fallbackProblemId = null;
            String problemIdRaw = request.getParameter("problemId");
            if (problemIdRaw != null && !problemIdRaw.isBlank()) {
                try {
                    fallbackProblemId = Long.valueOf(problemIdRaw);
                } catch (NumberFormatException ignored) {
                    // Ignore invalid id in fallback path.
                }
            }
            if (fallbackProblemId != null) {
                try {
                    request.setAttribute("problem", problemService.getProblemById(fallbackProblemId));
                } catch (ServiceException ignored) {
                    // Keep original error path if problem lookup fails.
                }
            }
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to add test case.",
                    "/jsp/admin/test-case-form.jsp"
            );
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        Object loggedIn = request.getSession(false) == null ? null : request.getSession(false).getAttribute("loggedInUser");
        if (!(loggedIn instanceof User)) {
            return false;
        }
        User user = (User) loggedIn;
        return user.getRole() == UserRole.ADMIN;
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

    private Integer parseInteger(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new ValidationException("Numeric fields are required.");
        }
        try {
            return Integer.valueOf(rawValue);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Numeric fields must be valid numbers.");
        }
    }

    private Problem buildProblemFromRequest(HttpServletRequest request) {
        Problem problem = new Problem();
        String idRaw = request.getParameter("id");
        if (idRaw != null && !idRaw.isBlank()) {
            try {
                problem.setId(Long.valueOf(idRaw));
            } catch (NumberFormatException ignored) {
                // Ignore invalid id in prefill object.
            }
        }
        problem.setTitle(request.getParameter("title"));
        problem.setDescription(request.getParameter("description"));
        String timeLimit = request.getParameter("timeLimit");
        if (timeLimit != null && !timeLimit.isBlank()) {
            try {
                problem.setTimeLimit(Integer.valueOf(timeLimit));
            } catch (NumberFormatException ignored) {
                // Ignore invalid value in prefill object.
            }
        }
        String memoryLimit = request.getParameter("memoryLimit");
        if (memoryLimit != null && !memoryLimit.isBlank()) {
            try {
                problem.setMemoryLimit(Integer.valueOf(memoryLimit));
            } catch (NumberFormatException ignored) {
                // Ignore invalid value in prefill object.
            }
        }
        return problem;
    }
}
