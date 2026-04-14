package dao;

import model.Problem;

import java.util.List;

/**
 * DAO contract for problem persistence operations.
 */
public interface ProblemDAO {

    List<Problem> getAllProblems();

    List<Problem> getProblems(int page, int size);

    long getTotalProblemCount();

    Problem getProblemById(Long id);
    
    Problem getProblemById(Long id, Boolean shouldInitialize);

    Problem createProblem(Problem problem);

    Problem updateProblem(Problem problem);

    void deleteProblem(Long id);
}
