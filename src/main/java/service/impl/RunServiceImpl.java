package service.impl;

import exception.ValidationException;
import judge.CodeExecutor;
import judge.ExecutionResult;
import judge.ExecutionStatus;
import judge.LanguageExecutorFactory;
import model.Language;
import model.TestCase;
import service.RunService;
import service.TestCaseService;

import java.util.List;

/**
 * Default implementation for one-off code execution.
 */
public class RunServiceImpl implements RunService {

    private final LanguageExecutorFactory executorFactory;
    private final TestCaseService testCaseService;

    public RunServiceImpl() {
        this.executorFactory = new LanguageExecutorFactory();
        this.testCaseService = new TestCaseServiceImpl();
    }

    @Override
    public ExecutionResult run(Long problemId, String language, String code) {
        if (problemId == null || problemId <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        String normalizedLanguage = safeTrim(language);
        String normalizedCode = safeTrim(code);

        if (normalizedLanguage.isEmpty()) {
            throw new ValidationException("Language is required.");
        }
        if (normalizedCode.isEmpty()) {
            throw new ValidationException("Code is required.");
        }

        Language parsedLanguage;
        try {
            parsedLanguage = Language.fromValue(normalizedLanguage);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unsupported language.");
        }

        CodeExecutor executor = executorFactory.getExecutor(parsedLanguage);
        List<TestCase> sampleTestCases = testCaseService.getSampleByProblemId(problemId);
        if (sampleTestCases.isEmpty()) {
            throw new ValidationException("No sample test cases available for this problem.");
        }

        int passedCount = 0;
        int totalCount = sampleTestCases.size();
        long totalExecutionTime = 0L;

        for (TestCase testCase : sampleTestCases) {
            ExecutionResult executionResult = executor.execute(
                    normalizedCode,
                    normalizeInputForExecution(testCase.getInput())
            );
            totalExecutionTime += executionResult.getExecutionTime();

            String actualOutputRaw = safe(executionResult.getOutput());
            String expectedOutputRaw = safe(testCase.getExpectedOutput());

            if (executionResult.getStatus() == ExecutionStatus.TIME_LIMIT_EXCEEDED) {
                ExecutionResult tleResult = buildBaseResult(
                        ExecutionStatus.TIME_LIMIT_EXCEEDED,
                        "",
                        safe(executionResult.getError()),
                        totalExecutionTime,
                        passedCount,
                        totalCount
                );
                return tleResult;
            }

            if (executionResult.getStatus() != ExecutionStatus.ACCEPTED) {
                ExecutionResult failedResult = buildBaseResult(
                        executionResult.getStatus(),
                        truncateDisplay(actualOutputRaw),
                        safe(executionResult.getError()),
                        totalExecutionTime,
                        passedCount,
                        totalCount
                );
                failedResult.setFailedInput(safe(testCase.getInput()));
                failedResult.setFailedExpectedOutput(truncateDisplay(expectedOutputRaw));
                failedResult.setFailedActualOutput(truncateDisplay(actualOutputRaw));
                return failedResult;
            }

            if (!normalizeOutput(actualOutputRaw).equals(normalizeOutput(expectedOutputRaw))) {
                ExecutionResult wrongResult = buildBaseResult(
                        ExecutionStatus.WRONG,
                        truncateDisplay(actualOutputRaw),
                        "",
                        totalExecutionTime,
                        passedCount,
                        totalCount
                );
                wrongResult.setFailedInput(safe(testCase.getInput()));
                wrongResult.setFailedExpectedOutput(truncateDisplay(expectedOutputRaw));
                wrongResult.setFailedActualOutput(truncateDisplay(actualOutputRaw));
                return wrongResult;
            }
            passedCount++;
        }

        return buildBaseResult(
                ExecutionStatus.ACCEPTED,
                "All sample test cases passed.",
                "",
                totalExecutionTime,
                passedCount,
                totalCount
        );
    }

    private ExecutionResult buildBaseResult(
            ExecutionStatus status,
            String output,
            String error,
            long executionTime,
            int passedCount,
            int totalCount
    ) {
        ExecutionResult result = new ExecutionResult(output, error, executionTime, status);
        result.setPassedCount(passedCount);
        result.setTotalCount(totalCount);
        return result;
    }

    private String normalizeInputForExecution(String input) {
        String trimmed = safeTrim(input);
        if (trimmed.isEmpty() || "NA".equalsIgnoreCase(trimmed)) {
            return "";
        }
        return input == null ? "" : input;
    }

    private String normalizeOutput(String value) {
        String normalized = safe(value).trim();
        return normalized.replaceAll("\\s+", " ");
    }

    private String truncateDisplay(String value) {
        String safeValue = safe(value).trim();
        if (safeValue.isEmpty()) {
            return safeValue;
        }
        String[] lines = safeValue.split("\\R");
        String limitedByLines = lines.length > 2 ? lines[0] + "\n" + lines[1] : safeValue;
        boolean truncatedByLines = lines.length > 2;

        String limitedByChars = limitedByLines;
        boolean truncatedByChars = false;
        if (limitedByLines.length() > 200) {
            limitedByChars = limitedByLines.substring(0, 200);
            truncatedByChars = true;
        }
        if (truncatedByLines || truncatedByChars) {
            return limitedByChars + "...";
        }
        return limitedByChars;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
