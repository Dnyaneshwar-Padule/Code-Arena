package service;

import judge.JudgeResult;

/**
 * Service contract for judging submissions.
 */
public interface JudgeService {

    JudgeResult judge(Long problemId, String code, String language);
}
