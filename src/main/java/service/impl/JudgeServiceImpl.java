package service.impl;

import exception.ServiceException;
import exception.ValidationException;
import judge.CodeExecutor;
import judge.ExecutionResult;
import judge.ExecutionStatus;
import judge.JudgeResult;
import judge.LanguageExecutorFactory;
import model.Language;
import model.Submission;
import model.SubmissionStatus;
import model.TestCase;
import service.JudgeService;
import service.TestCaseService;

import java.util.List;

/**
 * Default implementation of submission judging logic.
 */
public class JudgeServiceImpl implements JudgeService {

    private final TestCaseService testCaseService;
    private final LanguageExecutorFactory executorFactory;

    public JudgeServiceImpl() {
        this.testCaseService = new TestCaseServiceImpl();
        this.executorFactory = new LanguageExecutorFactory();
    }

    @Override
    public JudgeResult judge(Submission submission) {
        if (submission == null || submission.getProblem() == null || submission.getProblem().getId() == null) {
            throw new ValidationException("Submission problem is required.");
        }

        Language language;
        try {
            language = Language.fromValue(submission.getLanguage());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unsupported language.");
        }

        List<TestCase> testCases = testCaseService.getAllByProblemId(submission.getProblem().getId());
        if (testCases.isEmpty()) {
            return new JudgeResult(SubmissionStatus.ERROR, "", "No test cases configured.", 0L);
        }

        CodeExecutor executor = executorFactory.getExecutor(language);
        long totalTime = 0L;
        String lastOutput = "";
        for (TestCase testCase : testCases) {
            ExecutionResult executionResult;
            try {
                executionResult = executor.execute(submission.getCode(), testCase.getInput());
            } catch (Exception ex) {
                throw new ServiceException("Executor failed unexpectedly.", ex);
            }
            totalTime += executionResult.getExecutionTime();
            lastOutput = safe(executionResult.getOutput());

            if (executionResult.getStatus() != ExecutionStatus.ACCEPTED
                    && executionResult.getStatus() != ExecutionStatus.WRONG) {
                return new JudgeResult(
                        SubmissionStatus.ERROR,
                        lastOutput,
                        safe(executionResult.getError()).isBlank()
                                ? executionResult.getStatus().name()
                                : executionResult.getError(),
                        totalTime
                );
            }

            if (!normalize(lastOutput).equals(normalize(testCase.getExpectedOutput()))
                    || executionResult.getStatus() == ExecutionStatus.WRONG) {
                return new JudgeResult(SubmissionStatus.WRONG, lastOutput, null, totalTime);
            }
        }

        return new JudgeResult(SubmissionStatus.ACCEPTED, lastOutput, null, totalTime);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
