package dao;

import model.TestCase;

import java.util.List;

/**
 * DAO contract for test case persistence operations.
 */
public interface TestCaseDAO {

    TestCase createTestCase(TestCase testCase);

    TestCase getTestCaseById(Long id);

    List<TestCase> getAllByProblemId(Long problemId);

    List<TestCase> getSampleByProblemId(Long problemId);

    TestCase updateTestCase(TestCase testCase);

    void deleteTestCase(Long id);
}
