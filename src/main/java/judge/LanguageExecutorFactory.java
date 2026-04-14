package judge;

import model.Language;

/**
 * Factory for resolving a language-specific executor.
 */
public class LanguageExecutorFactory {

    public CodeExecutor getExecutor(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language is required.");
        }
        return switch (language) {
            case JAVA -> new JavaExecutor();
            case CPP -> new CppExecutor();
            case C -> new CExecutor();
            case PYTHON -> new PythonExecutor();
        };
    }
}
