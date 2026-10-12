package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FormatUtil {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private FormatUtil() {
    }

    public static String dateTime(LocalDateTime value) {
        return value == null ? "" : value.format(DATE_TIME);
    }
}
