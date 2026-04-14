package service;

import model.Submission;

import java.util.List;

/**
 * Service contract for submission lifecycle.
 */
public interface SubmissionService {

    Submission submit(Long userId, Long problemId, String code, String language);

    List<Submission> getUserSubmissions(Long problemId, Long userId);

    Submission getUserSubmissionById(Long submissionId, Long userId);
}
