package service.impl;

import dao.ProblemDAO;
import dao.impl.ProblemDAOImpl;
import exception.DaoException;
import exception.ServiceException;
import exception.ValidationException;
import model.Problem;
import model.ProblemDifficulty;
import service.ProblemService;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation for problem-related use cases.
 */
public class ProblemServiceImpl implements ProblemService {

    private static final Logger LOGGER = Logger.getLogger(ProblemServiceImpl.class.getName());
    private final ProblemDAO problemDAO;

    public ProblemServiceImpl() {
        this.problemDAO = new ProblemDAOImpl();
    }

    @Override
    public List<Problem> getAllProblems() {
        try {
            return problemDAO.getAllProblems();
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get all problems", ex);
            throw new ServiceException("Unable to load problems right now.", ex);
        }
    }

    @Override
    public List<Problem> getProblems(int page, int size) {
        if (page <= 0) {
            throw new ValidationException("Invalid page number.");
        }
        if (size <= 0) {
            throw new ValidationException("Invalid page size.");
        }
        try {
            return problemDAO.getProblems(page, size);
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get paginated problems", ex);
            throw new ServiceException("Unable to load problems right now.", ex);
        }
    }

    @Override
    public long getTotalProblemCount() {
        try {
            return problemDAO.getTotalProblemCount();
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get total problem count", ex);
            throw new ServiceException("Unable to load problem count right now.", ex);
        }
    }

    @Override
    public long getTotalPages(int size) {
        if (size <= 0) {
            throw new ValidationException("Invalid page size.");
        }
        try {
            long totalCount = problemDAO.getTotalProblemCount();
            if (totalCount == 0) {
                return 0;
            }
            return (totalCount + size - 1) / size;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get total problem pages", ex);
            throw new ServiceException("Unable to load problems right now.", ex);
        }
    }

    @Override
    public Problem getProblemById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        try {
            Problem problem = problemDAO.getProblemById(id);
            if (problem == null) {
                throw new ValidationException("Problem not found.");
            }
            return problem;
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get problem by id", ex);
            throw new ServiceException("Unable to load problem details.", ex);
        }
    }

    @Override
    public Problem createProblem(String title, String description, String difficulty, Integer timeLimit, Integer memoryLimit) {
        try {
            Problem problem = new Problem();
            applyProblemFields(problem, title, description, difficulty, timeLimit, memoryLimit);
            return problemDAO.createProblem(problem);
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to create problem", ex);
            throw new ServiceException("Unable to create problem right now.", ex);
        }
    }

    @Override
    public Problem updateProblem(Long id, String title, String description, String difficulty, Integer timeLimit, Integer memoryLimit) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        try {
            Problem existing = problemDAO.getProblemById(id);
            if (existing == null) {
                throw new ValidationException("Problem not found.");
            }
            applyProblemFields(existing, title, description, difficulty, timeLimit, memoryLimit);
            return problemDAO.updateProblem(existing);
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update problem", ex);
            throw new ServiceException("Unable to update problem right now.", ex);
        }
    }

    @Override
    public void deleteProblem(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid problem id.");
        }
        try {
            problemDAO.deleteProblem(id);
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete problem", ex);
            throw new ServiceException("Unable to delete problem right now.", ex);
        }
    }

    private void applyProblemFields(
            Problem problem,
            String title,
            String description,
            String difficulty,
            Integer timeLimit,
            Integer memoryLimit
    ) {
        String normalizedTitle = title == null ? "" : title.trim();
        String normalizedDescription = description == null ? "" : description.trim();
        String normalizedDifficulty = difficulty == null ? "" : difficulty.trim().toUpperCase(Locale.ROOT);

        if (normalizedTitle.isEmpty()) {
            throw new ValidationException("Title is required.");
        }
        if (normalizedDescription.isEmpty()) {
            throw new ValidationException("Description is required.");
        }
        if (timeLimit == null || timeLimit <= 0) {
            throw new ValidationException("Time limit must be a positive number.");
        }
        if (memoryLimit == null || memoryLimit <= 0) {
            throw new ValidationException("Memory limit must be a positive number.");
        }

        ProblemDifficulty parsedDifficulty;
        try {
            parsedDifficulty = ProblemDifficulty.valueOf(normalizedDifficulty);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid difficulty. Use EASY, MEDIUM, or HARD.");
        }

        problem.setTitle(normalizedTitle);
        problem.setDescription(normalizedDescription);
        problem.setDifficulty(parsedDifficulty);
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit);
    }
}
