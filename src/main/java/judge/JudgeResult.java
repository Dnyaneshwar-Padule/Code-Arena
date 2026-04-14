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
    private int passedCount;
    private int totalCount;
    private String failedInput;
    private String failedExpectedOutput;
    private String failedActualOutput;

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

    public int getPassedCount() {
        return passedCount;
    }

    public void setPassedCount(int passedCount) {
        this.passedCount = passedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getFailedInput() {
        return failedInput;
    }

    public void setFailedInput(String failedInput) {
        this.failedInput = failedInput;
    }

    public String getFailedExpectedOutput() {
        return failedExpectedOutput;
    }

    public void setFailedExpectedOutput(String failedExpectedOutput) {
        this.failedExpectedOutput = failedExpectedOutput;
    }

    public String getFailedActualOutput() {
        return failedActualOutput;
    }

    public void setFailedActualOutput(String failedActualOutput) {
        this.failedActualOutput = failedActualOutput;
    }
}
