package service.impl;

import dao.ProblemDAO;
import dao.impl.ProblemDAOImpl;
import exception.ServiceException;
import exception.ValidationException;
import judge.CodeExecutor;
import judge.ExecutionResult;
import judge.ExecutionStatus;
import judge.JudgeResult;
import judge.LanguageExecutorFactory;
import model.Language;
import model.Problem;
import model.SubmissionStatus;
import model.TestCase;
import service.JudgeService;
import service.TestCaseService;

import java.util.List;

import org.hibernate.Hibernate;

/**
 * Default implementation of submission judging logic.
 */
public class JudgeServiceImpl implements JudgeService {

    private final ProblemDAO problemDAO;
    private final TestCaseService testCaseService;
    private final LanguageExecutorFactory executorFactory;

    public JudgeServiceImpl() {
        this.problemDAO = new ProblemDAOImpl();
        this.testCaseService = new TestCaseServiceImpl();
        this.executorFactory = new LanguageExecutorFactory();
    }

    @Override
    public JudgeResult judge(Long problemId, String code, String languageRaw) {
        if (problemId == null || problemId <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Code is required.");
        }
        if (languageRaw == null || languageRaw.trim().isEmpty()) {
            throw new ValidationException("Language is required.");
        }

        Language language;
        try {
            language = Language.fromValue(languageRaw.trim());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unsupported language.");
        }

        Problem problem = problemDAO.getProblemById(problemId);
        if (problem == null) {
            throw new ValidationException("Problem not found.");
        }

        
        List<TestCase> testCases = testCaseService.getAllByProblemId(problemId);
        
        if (testCases.isEmpty()) {
            JudgeResult emptyResult = new JudgeResult(SubmissionStatus.ERROR, "", "No test cases configured.", 0L);
            emptyResult.setPassedCount(0);
            emptyResult.setTotalCount(0);
            return emptyResult;
        }

        CodeExecutor executor = executorFactory.getExecutor(language);
        long totalTime = 0L;
        String lastOutput = "";
        int passedCount = 0;
        int totalCount = testCases.size();
        applyProblemLimits(problem);
        try {
            for (TestCase testCase : testCases) {
                ExecutionResult executionResult;
                try {
                    executionResult = executor.execute(code, normalizeInputForExecution(testCase.getInput()));
                } catch (Exception ex) {
                    throw new ServiceException("Executor failed unexpectedly.", ex);
                }
                totalTime += executionResult.getExecutionTime();
                lastOutput = safe(executionResult.getOutput());

                if (executionResult.getStatus() != ExecutionStatus.ACCEPTED
                        && executionResult.getStatus() != ExecutionStatus.WRONG) {
                    JudgeResult failedResult = new JudgeResult(
                            mapExecutionStatusToSubmissionStatus(executionResult.getStatus()),
                            lastOutput,
                            safe(executionResult.getError()).isBlank()
                                    ? executionResult.getStatus().name()
                                    : executionResult.getError(),
                            totalTime
                    );
                    applyProgressDetails(
                            failedResult,
                            passedCount,
                            totalCount,
                            testCase
                    );
                    return failedResult;
                }

                if (!normalizeOutput(lastOutput).equals(normalizeOutput(testCase.getExpectedOutput()))
                        || executionResult.getStatus() == ExecutionStatus.WRONG) {
                    JudgeResult wrongResult = new JudgeResult(SubmissionStatus.WRONG, lastOutput, null, totalTime);
                    applyProgressDetails(
                            wrongResult,
                            passedCount,
                            totalCount,
                            testCase
                    );
                    return wrongResult;
                }
                passedCount++;
            }
        } finally {
            clearProblemLimits();
        }

        JudgeResult acceptedResult = new JudgeResult(SubmissionStatus.ACCEPTED, lastOutput, null, totalTime);
        acceptedResult.setPassedCount(passedCount);
        acceptedResult.setTotalCount(totalCount);
        return acceptedResult;
    }

    private SubmissionStatus mapExecutionStatusToSubmissionStatus(ExecutionStatus executionStatus) {
        if (executionStatus == null) {
            return SubmissionStatus.ERROR;
        }
        return switch (executionStatus) {
            case TIME_LIMIT_EXCEEDED -> SubmissionStatus.TIME_LIMIT_EXCEEDED;
            case COMPILATION_ERROR -> SubmissionStatus.COMPILATION_ERROR;
            case RUNTIME_ERROR, MEMORY_LIMIT_EXCEEDED, OUTPUT_LIMIT_EXCEEDED -> SubmissionStatus.RUNTIME_ERROR;
            default -> SubmissionStatus.ERROR;
        };
    }

    private void applyProgressDetails(
            JudgeResult result,
            int passedCount,
            int totalCount,
            TestCase failedTestCase
    ) {
        result.setPassedCount(passedCount);
        result.setTotalCount(totalCount);
        if (Boolean.TRUE.equals(failedTestCase.getSample())) {
            result.setFailedInput(safe(failedTestCase.getInput()));
            result.setFailedExpectedOutput(safe(failedTestCase.getExpectedOutput()));
            result.setFailedActualOutput(safe(result.getOutput()));
        } else {
            result.setFailedInput("");
            result.setFailedExpectedOutput("");
            result.setFailedActualOutput("");
        }
    }

    private String normalizeInputForExecution(String value) {
        String normalized = value == null ? "" : value.trim();
        return "NA".equalsIgnoreCase(normalized) ? "" : value == null ? "" : value;
    }

    private String normalizeOutput(String value) {
        String normalized = value == null ? "" : value.trim();
        return normalized.replaceAll("\\s+", " ");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void applyProblemLimits(Problem problem) {
        Integer problemTimeLimit = problem.getTimeLimit();
        Integer problemMemoryLimit = problem.getMemoryLimit();
        if (problemTimeLimit != null && problemTimeLimit > 0) {
            System.setProperty("codearena.problem.time.limit.ms", String.valueOf(problemTimeLimit));
        }
        if (problemMemoryLimit != null && problemMemoryLimit > 0) {
            System.setProperty("codearena.problem.memory.limit.kb", String.valueOf(problemMemoryLimit));
        }
    }

    private void clearProblemLimits() {
        System.clearProperty("codearena.problem.time.limit.ms");
        System.clearProperty("codearena.problem.memory.limit.kb");
    }
}
