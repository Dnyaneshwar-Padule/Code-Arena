package judge;

/**
 * Execution status for a single test run.
 */
public enum ExecutionStatus {
    ACCEPTED,
    WRONG,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    RUNTIME_ERROR,
    COMPILATION_ERROR,
    OUTPUT_LIMIT_EXCEEDED
}
