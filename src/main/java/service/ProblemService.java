package service;

import model.Problem;

import java.util.List;

/**
 * Service contract for problem use cases.
 */
public interface ProblemService {

    List<Problem> getAllProblems();

    List<Problem> getProblems(int page, int size);

    long getTotalPages(int size);

    Problem getProblemById(Long id);

    Problem createProblem(String title, String description, String difficulty, Integer timeLimit, Integer memoryLimit);

    Problem updateProblem(Long id, String title, String description, String difficulty, Integer timeLimit, Integer memoryLimit);

    void deleteProblem(Long id);
}
