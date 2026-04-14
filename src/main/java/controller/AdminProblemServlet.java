package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Problem;
import model.TestCase;
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
                "/admin/problem/testcase",
                "/admin/problem/testcase/edit",
                "/admin/problem/testcase/update",
                "/admin/problem/testcase/delete"
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
            case "/admin/problem" -> showProblemDetail(request, response);
            case "/admin/problem/create" -> {
                request.setAttribute("formMode", "create");
                request.getRequestDispatcher("/jsp/admin/problem-form.jsp").forward(request, response);
            }
            case "/admin/problem/edit" -> showEditForm(request, response);
            case "/admin/problem/delete" -> deleteProblem(request, response);
            case "/admin/problem/testcase" -> showTestCaseForm(request, response);
            case "/admin/problem/testcase/edit" -> showEditTestCaseForm(request, response);
            case "/admin/problem/testcase/delete" -> deleteTestCase(request, response);
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
            case "/admin/problem/testcase/update" -> updateTestCase(request, response);
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

    private void showProblemDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long id = parseLong(request.getParameter("id"));
            Problem problem = problemService.getProblemById(id);
            request.setAttribute("problem", problem);
            request.setAttribute("testCases", testCaseService.getAllByProblemId(id));
            request.getRequestDispatcher("/jsp/admin/problem-detail.jsp").forward(request, response);
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load problem details.",
                    "/jsp/admin/problems.jsp"
            );
        }
    }

    private void createProblem(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            problemService.createProblem(
                    getTrimmedParameter(request, "title"),
                    getTrimmedParameter(request, "description"),
                    getTrimmedParameter(request, "difficulty"),
                    parseInteger(getTrimmedParameter(request, "timeLimit")),
                    parseInteger(getTrimmedParameter(request, "memoryLimit")),
                    getTrimmedParameter(request, "inputFormat"),
                    getTrimmedParameter(request, "outputFormat"),
                    getTrimmedParameter(request, "constraints")
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
                    getTrimmedParameter(request, "title"),
                    getTrimmedParameter(request, "description"),
                    getTrimmedParameter(request, "difficulty"),
                    parseInteger(getTrimmedParameter(request, "timeLimit")),
                    parseInteger(getTrimmedParameter(request, "memoryLimit")),
                    getTrimmedParameter(request, "inputFormat"),
                    getTrimmedParameter(request, "outputFormat"),
                    getTrimmedParameter(request, "constraints")
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
            request.setAttribute("formMode", "create");
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

    private void showEditTestCaseForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long testCaseId = parseLong(request.getParameter("id"));
            TestCase testCase = testCaseService.getTestCaseById(testCaseId);
            request.setAttribute("problem", testCase.getProblem());
            request.setAttribute("testCase", testCase);
            request.setAttribute("formMode", "edit");
            request.getRequestDispatcher("/jsp/admin/test-case-form.jsp").forward(request, response);
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load test case for editing.",
                    "/jsp/admin/problems.jsp"
            );
        }
    }

    private void addTestCase(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            Long problemId = parseLong(getTrimmedParameter(request, "problemId"));
            boolean isSample = "on".equalsIgnoreCase(getTrimmedParameter(request, "isSample"));
            testCaseService.addTestCase(
                    problemId,
                    getTrimmedParameter(request, "input"),
                    getTrimmedParameter(request, "expectedOutput"),
                    isSample
            );
            response.sendRedirect(request.getContextPath() + "/admin/problem?id=" + problemId);
        } catch (ServiceException ex) {
            Long fallbackProblemId = null;
            String problemIdRaw = getTrimmedParameter(request, "problemId");
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

    private void updateTestCase(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Long fallbackProblemId = null;
        try {
            Long testCaseId = parseLong(getTrimmedParameter(request, "id"));
            Long problemId = parseLong(getTrimmedParameter(request, "problemId"));
            boolean isSample = "on".equalsIgnoreCase(getTrimmedParameter(request, "isSample"));
            fallbackProblemId = problemId;
            testCaseService.updateTestCase(
                    testCaseId,
                    getTrimmedParameter(request, "input"),
                    getTrimmedParameter(request, "expectedOutput"),
                    isSample
            );
            response.sendRedirect(request.getContextPath() + "/admin/problem?id=" + problemId);
        } catch (ServiceException ex) {
            if (fallbackProblemId != null) {
                try {
                    request.setAttribute("problem", problemService.getProblemById(fallbackProblemId));
                } catch (ServiceException ignored) {
                    // Keep original error path if problem lookup fails.
                }
            }
            request.setAttribute("formMode", "edit");
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to update test case.",
                    "/jsp/admin/test-case-form.jsp"
            );
        }
    }

    private void deleteTestCase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String problemIdRaw = getTrimmedParameter(request, "problemId");
        String redirectUrl = request.getContextPath() + "/admin/problems";
        if (problemIdRaw != null && !problemIdRaw.isBlank()) {
            redirectUrl = request.getContextPath() + "/admin/problem?id=" + problemIdRaw;
        }
        try {
            Long testCaseId = parseLong(request.getParameter("id"));
            testCaseService.deleteTestCase(testCaseId);
        } catch (ServiceException ex) {
            // Keep delete flow simple: redirect back with graceful no-op on failure.
        }
        response.sendRedirect(redirectUrl);
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
        String trimmedValue = rawValue == null ? null : rawValue.trim();
        if (trimmedValue == null || trimmedValue.isBlank()) {
            throw new ValidationException("Invalid problem id.");
        }
        try {
            return Long.valueOf(trimmedValue);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Invalid problem id.");
        }
    }

    private Integer parseInteger(String rawValue) {
        String trimmedValue = rawValue == null ? null : rawValue.trim();
        if (trimmedValue == null || trimmedValue.isBlank()) {
            throw new ValidationException("Numeric fields are required.");
        }
        try {
            return Integer.valueOf(trimmedValue);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Numeric fields must be valid numbers.");
        }
    }

    private Problem buildProblemFromRequest(HttpServletRequest request) {
        Problem problem = new Problem();
        String idRaw = getTrimmedParameter(request, "id");
        if (idRaw != null && !idRaw.isBlank()) {
            try {
                problem.setId(Long.valueOf(idRaw));
            } catch (NumberFormatException ignored) {
                // Ignore invalid id in prefill object.
            }
        }
        problem.setTitle(getTrimmedParameter(request, "title"));
        problem.setDescription(getTrimmedParameter(request, "description"));
        problem.setInputFormat(getTrimmedParameter(request, "inputFormat"));
        problem.setOutputFormat(getTrimmedParameter(request, "outputFormat"));
        problem.setConstraints(getTrimmedParameter(request, "constraints"));
        String timeLimit = getTrimmedParameter(request, "timeLimit");
        if (timeLimit != null && !timeLimit.isBlank()) {
            try {
                problem.setTimeLimit(Integer.valueOf(timeLimit));
            } catch (NumberFormatException ignored) {
                // Ignore invalid value in prefill object.
            }
        }
        String memoryLimit = getTrimmedParameter(request, "memoryLimit");
        if (memoryLimit != null && !memoryLimit.isBlank()) {
            try {
                problem.setMemoryLimit(Integer.valueOf(memoryLimit));
            } catch (NumberFormatException ignored) {
                // Ignore invalid value in prefill object.
            }
        }
        return problem;
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }
}
