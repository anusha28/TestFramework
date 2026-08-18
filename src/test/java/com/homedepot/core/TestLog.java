package com.homedepot.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes to stdout so step progress shows up in {@code mvn test} output
 * even if SLF4J has no binding.
 */
public final class TestLog {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private TestLog() {
    }

    public static void info(String message) {
        System.out.println(prefix("INFO") + message);
    }

    public static void error(String message) {
        System.err.println(prefix("ERROR") + message);
    }

    public static void error(String message, Throwable throwable) {
        error(message);
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    private static String prefix(String level) {
        return TIME.format(LocalDateTime.now()) + " [" + level + "] ";
    }
}
