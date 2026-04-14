package dao;

import model.Submission;

/**
 * DAO contract for submission persistence operations.
 */
public interface SubmissionDAO {

    Submission createSubmission(Submission submission);

    Submission updateSubmission(Submission submission);

    Submission getSubmissionById(Long id);
}
