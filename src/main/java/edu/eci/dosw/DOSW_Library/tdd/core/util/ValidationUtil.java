package edu.eci.dosw.DOSW_Library.tdd.core.util;

public class ValidationUtil {
    private ValidationUtil() {
    }

    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNonEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
