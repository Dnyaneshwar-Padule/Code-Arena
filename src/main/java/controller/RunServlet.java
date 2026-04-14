package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import judge.ExecutionResult;
import judge.ExecutionStatus;
import model.User;
import service.RunService;
import service.impl.RunServiceImpl;

import java.io.IOException;

@WebServlet(name = "RunServlet", urlPatterns = "/run")
public class RunServlet extends HttpServlet {

    private transient RunService runService;

    @Override
    public void init() throws ServletException {
        this.runService = new RunServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User loggedInUser = getLoggedInUser(request);
        if (loggedInUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(response, "{\"error\":\"Unauthorized\"}");
            return;
        }

        try {
            Long problemId = parseLong(getTrimmedParameter(request, "problemId"));
            String code = getTrimmedParameter(request, "code");
            String language = getTrimmedParameter(request, "language");

            ExecutionResult result = runService.run(problemId, language, code);
            String json = "{"
                    + "\"status\":\"" + escapeJson(mapRunStatus(result.getStatus())) + "\","
                    + "\"rawStatus\":\"" + escapeJson(result.getStatus().name()) + "\","
                    + "\"output\":\"" + escapeJson(result.getOutput()) + "\","
                    + "\"error\":\"" + escapeJson(result.getError()) + "\","
                    + "\"executionTime\":" + result.getExecutionTime() + ","
                    + "\"passedCount\":" + result.getPassedCount() + ","
                    + "\"totalCount\":" + result.getTotalCount() + ","
                    + "\"failedInput\":\"" + escapeJson(result.getFailedInput()) + "\","
                    + "\"failedExpectedOutput\":\"" + escapeJson(result.getFailedExpectedOutput()) + "\","
                    + "\"failedActualOutput\":\"" + escapeJson(result.getFailedActualOutput()) + "\""
                    + "}";
            writeJson(response, json);
        } catch (ValidationException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, "{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        } catch (ServiceException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, "{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, "{\"error\":\"Unable to run code right now.\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
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

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
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

    private String mapRunStatus(ExecutionStatus status) {
        if (status == null) {
            return "ERROR";
        }
        return switch (status) {
            case ACCEPTED -> "ACCEPTED";
            case WRONG -> "WRONG";
            case TIME_LIMIT_EXCEEDED -> "TLE";
            default -> "ERROR";
        };
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
