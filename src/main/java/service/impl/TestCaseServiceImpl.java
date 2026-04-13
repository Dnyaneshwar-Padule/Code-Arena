package service.impl;

import exception.ServiceException;
import exception.ValidationException;
import model.Problem;
import model.TestCase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import service.TestCaseService;
import util.HibernateUtil;
import validation.TestCaseValidator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation for test case management use cases.
 */
public class TestCaseServiceImpl implements TestCaseService {

    private static final Logger LOGGER = Logger.getLogger(TestCaseServiceImpl.class.getName());

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

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Problem problem = session.get(Problem.class, problemId);
            if (problem == null) {
                throw new ValidationException("Problem not found.");
            }

            TestCase testCase = new TestCase();
            testCase.setProblem(problem);
            testCase.setInput(input.trim());
            testCase.setExpectedOutput(expectedOutput.trim());
            testCase.setSample(isSample);

            session.persist(testCase);
            transaction.commit();
            return testCase;
        } catch (ValidationException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Failed to add test case", ex);
            throw new ServiceException("Unable to add test case right now.", ex);
        }
    }
}
