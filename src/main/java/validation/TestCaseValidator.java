package validation;

import exception.ValidationException;

/**
 * Validator for test case input/output payloads.
 */
public final class TestCaseValidator {

    private static final int MAX_INPUT_LENGTH = 10_000;
    private static final int MAX_OUTPUT_LENGTH = 10_000;

    private TestCaseValidator() {
        // Utility class
    }

    public static void validate(String input, String expectedOutput) {
        String normalizedInput = input == null ? "" : input.trim();
        String normalizedOutput = expectedOutput == null ? "" : expectedOutput.trim();

        if (normalizedInput.isEmpty()) {
            throw new ValidationException("Test case input is required.");
        }
        if (normalizedOutput.isEmpty()) {
            throw new ValidationException("Expected output is required.");
        }
        if (normalizedInput.length() > MAX_INPUT_LENGTH) {
            throw new ValidationException("Test case input exceeds the allowed size limit.");
        }
        if (normalizedOutput.length() > MAX_OUTPUT_LENGTH) {
            throw new ValidationException("Expected output exceeds the allowed size limit.");
        }

        // Optional format safety check for unexpected null characters.
        if (normalizedInput.contains("\u0000") || normalizedOutput.contains("\u0000")) {
            throw new ValidationException("Input/output contains invalid characters.");
        }
    }
}
