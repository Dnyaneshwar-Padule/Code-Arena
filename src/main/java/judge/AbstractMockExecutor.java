package judge;

/**
 * Shared mock behavior for all language executors.
 */
abstract class AbstractMockExecutor implements CodeExecutor {

    @Override
    public ExecutionResult execute(String code, String input) {
        long startedAt = System.currentTimeMillis();
        String normalizedCode = code == null ? "" : code;

        if (normalizedCode.contains("RUNTIME_ERROR")) {
            return new ExecutionResult("", "Runtime error during mocked execution.", elapsed(startedAt), ExecutionStatus.RUNTIME_ERROR);
        }

        if (normalizedCode.contains("WRONG_ANSWER")) {
            return new ExecutionResult("wrong_output", "", elapsed(startedAt), ExecutionStatus.WRONG);
        }

        String output = mockOutput(input);
        return new ExecutionResult(output, "", elapsed(startedAt), ExecutionStatus.ACCEPTED);
    }

    protected String mockOutput(String input) {
        return input == null ? "" : input.trim();
    }

    private long elapsed(long startedAt) {
        return Math.max(1L, System.currentTimeMillis() - startedAt);
    }
}
