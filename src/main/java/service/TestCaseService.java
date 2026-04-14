package service;

import model.TestCase;

import java.util.List;

/**
 * Service contract for test case management.
 */
public interface TestCaseService {

    TestCase addTestCase(Long problemId, String input, String expectedOutput);

    TestCase addTestCase(Long problemId, String input, String expectedOutput, boolean isSample);

    List<TestCase> getAllByProblemId(Long problemId);

    List<TestCase> getSampleByProblemId(Long problemId);

    TestCase getTestCaseById(Long testCaseId);

    TestCase updateTestCase(Long testCaseId, String input, String expectedOutput, boolean isSample);

    void deleteTestCase(Long testCaseId);
}
