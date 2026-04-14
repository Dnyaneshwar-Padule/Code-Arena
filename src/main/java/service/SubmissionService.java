package service;

import model.Submission;

/**
 * Service contract for submission lifecycle.
 */
public interface SubmissionService {

    Submission submit(Long userId, Long problemId, String code, String language);
}
