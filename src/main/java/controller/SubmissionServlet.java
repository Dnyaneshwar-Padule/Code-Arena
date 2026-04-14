package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Submission;
import model.User;
import service.SubmissionService;
import service.impl.SubmissionServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "SubmissionServlet", urlPatterns = {"/submit", "/submissions", "/submission"})
public class SubmissionServlet extends HttpServlet {

    private transient SubmissionService submissionService;

    @Override
    public void init() throws ServletException {
        this.submissionService = new SubmissionServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = getLoggedInUser(request);
        if (loggedInUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(response, "{\"error\":\"Unauthorized\"}");
            return;
        }

        try {
            Long problemId = parseLong(getTrimmedParameter(request, "problemId"));
            Long contestId = parseOptionalLong(getTrimmedParameter(request, "contestId"));
            String code = getTrimmedParameter(request, "code");
            String language = getTrimmedParameter(request, "language");

            Submission submission = submissionService.submit(loggedInUser.getId(), problemId, code, language, contestId);
            String json = "{"
                    + "\"status\":\"" + escapeJson(String.valueOf(submission.getStatus())) + "\","
                    + "\"executionTime\":" + safeNumber(submission.getExecutionTime()) + ","
                    + "\"passedCount\":" + safeNumber(submission.getPassedCount()) + ","
                    + "\"totalCount\":" + safeNumber(submission.getTotalCount()) + ","
                    + "\"output\":\"" + escapeJson(submission.getOutput()) + "\","
                    + "\"error\":\"" + escapeJson(submission.getErrorMessage()) + "\""
                    + "}";
            writeJson(response, json);
            return;
        } catch (ValidationException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, "{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        } catch (ServiceException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, "{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, "{\"error\":\"Unable to process submission right now.\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = getLoggedInUser(request);
        if (loggedInUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(response, "{\"error\":\"Unauthorized\"}");
            return;
        }

        try {
            String servletPath = request.getServletPath();
            if ("/submission".equals(servletPath)) {
                Long submissionId = parseLong(getTrimmedParameter(request, "id"));
                Submission submission = submissionService.getUserSubmissionById(submissionId, loggedInUser.getId());
                writeJson(response, buildSubmissionDetailJson(submission));
                return;
            }

            Long problemId = parseLong(getTrimmedParameter(request, "problemId"));
            List<Submission> submissions = submissionService.getUserSubmissions(problemId, loggedInUser.getId());
            writeJson(response, buildSubmissionsJson(submissions));
        } catch (ValidationException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, "{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        } catch (ServiceException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, "{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, "{\"error\":\"Unable to load submission history right now.\"}");
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

    private Long parseOptionalLong(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(rawValue);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Invalid contest id.");
        }
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    private String safeNumber(Number value) {
        return value == null ? "0" : String.valueOf(value);
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

    private String buildSubmissionsJson(List<Submission> submissions) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"submissions\":[");
        for (int i = 0; i < submissions.size(); i++) {
            Submission submission = submissions.get(i);
            if (i > 0) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("{")
                    .append("\"id\":").append(safeNumber(submission.getId())).append(",")
                    .append("\"status\":\"").append(escapeJson(String.valueOf(submission.getStatus()))).append("\",")
                    .append("\"language\":\"").append(escapeJson(submission.getLanguage())).append("\",")
                    .append("\"executionTime\":").append(safeNumber(submission.getExecutionTime())).append(",")
                    .append("\"createdAt\":\"").append(escapeJson(formatDateTime(submission.getCreatedAt()))).append("\"")
                    .append("}");
        }
        jsonBuilder.append("]}");
        return jsonBuilder.toString();
    }

    private String buildSubmissionDetailJson(Submission submission) {
        return "{"
                + "\"id\":" + safeNumber(submission.getId()) + ","
                + "\"status\":\"" + escapeJson(String.valueOf(submission.getStatus())) + "\","
                + "\"language\":\"" + escapeJson(submission.getLanguage()) + "\","
                + "\"executionTime\":" + safeNumber(submission.getExecutionTime()) + ","
                + "\"createdAt\":\"" + escapeJson(formatDateTime(submission.getCreatedAt())) + "\","
                + "\"code\":\"" + escapeJson(submission.getCode()) + "\","
                + "\"output\":\"" + escapeJson(submission.getOutput()) + "\","
                + "\"error\":\"" + escapeJson(submission.getErrorMessage()) + "\""
                + "}";
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "-";
        }
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
