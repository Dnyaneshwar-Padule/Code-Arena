package service;

import model.TestCase;

/**
 * Service contract for test case management.
 */
public interface TestCaseService {

    TestCase addTestCase(Long problemId, String input, String expectedOutput);

    TestCase addTestCase(Long problemId, String input, String expectedOutput, boolean isSample);
}
