package judge;

/**
 * Result of executing code against one input.
 */
public class ExecutionResult {

    private String output;
    private long executionTime;
    private ExecutionStatus status;

    public ExecutionResult() {
    }

    public ExecutionResult(String output, long executionTime, ExecutionStatus status) {
        this.output = output;
        this.executionTime = executionTime;
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
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
}
