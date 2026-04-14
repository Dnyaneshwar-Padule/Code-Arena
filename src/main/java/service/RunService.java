package service;

import judge.ExecutionResult;

/**
 * Service contract for one-off code execution (Run).
 */
public interface RunService {

    ExecutionResult run(Long problemId, String language, String code);
}
