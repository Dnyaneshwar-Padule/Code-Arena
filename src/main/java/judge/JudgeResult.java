package judge;

import model.SubmissionStatus;

/**
 * Aggregated result from judging a full submission.
 */
public class JudgeResult {

    private SubmissionStatus finalStatus;
    private String output;
    private String errorMessage;
    private long executionTime;

    public JudgeResult() {
    }

    public JudgeResult(SubmissionStatus finalStatus, String output, String errorMessage, long executionTime) {
        this.finalStatus = finalStatus;
        this.output = output;
        this.errorMessage = errorMessage;
        this.executionTime = executionTime;
    }

    public SubmissionStatus getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(SubmissionStatus finalStatus) {
        this.finalStatus = finalStatus;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
