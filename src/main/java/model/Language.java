package model;

import java.util.Locale;

/**
 * Supported submission languages.
 */
public enum Language {
    C,
    CPP,
    JAVA,
    PYTHON;

    public static Language fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Language is required.");
        }
        return Language.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
