package dao;

import model.Submission;

import java.util.List;

/**
 * DAO contract for submission persistence operations.
 */
public interface SubmissionDAO {

    Submission createSubmission(Submission submission);

    Submission updateSubmission(Submission submission);

    Submission getSubmissionById(Long id);

    List<Submission> getByUserAndProblem(Long userId, Long problemId);
}
