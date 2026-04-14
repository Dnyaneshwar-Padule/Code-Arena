package service;

import judge.JudgeResult;
import model.Submission;

/**
 * Service contract for judging submissions.
 */
public interface JudgeService {

    JudgeResult judge(Submission submission);
}
