package judge;

/**
 * Language-specific executor contract.
 */
public interface CodeExecutor {

    ExecutionResult execute(String code, String input);
}
