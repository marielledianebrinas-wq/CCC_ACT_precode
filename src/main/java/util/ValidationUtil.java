package util;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static void requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    public static void requireEmail(String value) {
        requireText(value, "Email");
        if (!value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
    }

    public static void requireAcademicYear(String value) {
        requireText(value, "Academic year");
        if (!value.matches("^\\d{4}\\u2013\\d{4}$")) {
            throw new IllegalArgumentException("Academic year must use the format 2026\u20132027.");
        }
    }
}
