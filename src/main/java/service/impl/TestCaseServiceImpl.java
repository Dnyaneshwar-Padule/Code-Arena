package service.impl;

import dao.ProblemDAO;
import dao.TestCaseDAO;
import dao.impl.ProblemDAOImpl;
import dao.impl.TestCaseDAOImpl;
import exception.DaoException;
import exception.ServiceException;
import exception.ValidationException;
import model.Problem;
import model.TestCase;
import service.TestCaseService;
import validation.TestCaseValidator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation for test case management use cases.
 */
public class TestCaseServiceImpl implements TestCaseService {

    private static final Logger LOGGER = Logger.getLogger(TestCaseServiceImpl.class.getName());
    private final ProblemDAO problemDAO;
    private final TestCaseDAO testCaseDAO;

    public TestCaseServiceImpl() {
        this.problemDAO = new ProblemDAOImpl();
        this.testCaseDAO = new TestCaseDAOImpl();
    }

    @Override
    public TestCase addTestCase(Long problemId, String input, String expectedOutput) {
        return addTestCase(problemId, input, expectedOutput, false);
    }

    @Override
    public TestCase addTestCase(Long problemId, String input, String expectedOutput, boolean isSample) {
        if (problemId == null || problemId <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        TestCaseValidator.validate(input, expectedOutput);

        try {
            Problem problem = problemDAO.getProblemById(problemId);
            if (problem == null) {
                throw new ValidationException("Problem not found.");
            }

            TestCase testCase = new TestCase();
            testCase.setProblem(problem);
            testCase.setInput(input.trim());
            testCase.setExpectedOutput(expectedOutput.trim());
            testCase.setSample(isSample);

            return testCaseDAO.createTestCase(testCase);
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to add test case", ex);
            throw new ServiceException("Unable to add test case right now.", ex);
        }
    }

    @Override
    public List<TestCase> getAllByProblemId(Long problemId) {
        validateProblemId(problemId);
        try {
            return testCaseDAO.getAllByProblemId(problemId);
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch all test cases", ex);
            throw new ServiceException("Unable to load test cases right now.", ex);
        }
    }

    @Override
    public List<TestCase> getSampleByProblemId(Long problemId) {
        validateProblemId(problemId);
        try {
            return testCaseDAO.getSampleByProblemId(problemId);
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch sample test cases", ex);
            throw new ServiceException("Unable to load sample test cases right now.", ex);
        }
    }

    @Override
    public TestCase getTestCaseById(Long testCaseId) {
        validateTestCaseId(testCaseId);
        try {
            TestCase testCase = testCaseDAO.getTestCaseById(testCaseId);
            if (testCase == null) {
                throw new ValidationException("Test case not found.");
            }
            return testCase;
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to fetch test case by id", ex);
            throw new ServiceException("Unable to load test case right now.", ex);
        }
    }

    @Override
    public TestCase updateTestCase(Long testCaseId, String input, String expectedOutput, boolean isSample) {
        validateTestCaseId(testCaseId);
        TestCaseValidator.validate(input, expectedOutput);
        try {
            TestCase existing = testCaseDAO.getTestCaseById(testCaseId);
            if (existing == null) {
                throw new ValidationException("Test case not found.");
            }
            existing.setInput(input.trim());
            existing.setExpectedOutput(expectedOutput.trim());
            existing.setSample(isSample);
            return testCaseDAO.updateTestCase(existing);
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update test case", ex);
            throw new ServiceException("Unable to update test case right now.", ex);
        }
    }

    @Override
    public void deleteTestCase(Long testCaseId) {
        validateTestCaseId(testCaseId);
        try {
            testCaseDAO.deleteTestCase(testCaseId);
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete test case", ex);
            throw new ServiceException("Unable to delete test case right now.", ex);
        }
    }

    private void validateProblemId(Long problemId) {
        if (problemId == null || problemId <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
    }

    private void validateTestCaseId(Long testCaseId) {
        if (testCaseId == null || testCaseId <= 0) {
            throw new ValidationException("Invalid test case id.");
        }
    }
}
