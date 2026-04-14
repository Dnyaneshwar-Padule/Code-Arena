package judge;

/**
 * Result of executing code against one input.
 */
public class ExecutionResult {

    private String output;
    private String error;
    private long executionTime;
    private ExecutionStatus status;
    private int passedCount;
    private int totalCount;
    private String failedInput;
    private String failedExpectedOutput;
    private String failedActualOutput;

    public ExecutionResult() {
    }

    public ExecutionResult(String output, String error, long executionTime, ExecutionStatus status) {
        this.output = output;
        this.error = error;
        this.executionTime = executionTime;
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
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
