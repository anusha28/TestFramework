package com.homedepot.core;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {

    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
    }

    public static Path capture(WebDriver driver, String label) {
        if (driver == null) {
            TestLog.error("Cannot capture screenshot '" + label + "': driver is null");
            return null;
        }
        if (!(driver instanceof TakesScreenshot takesScreenshot)) {
            TestLog.error("Cannot capture screenshot '" + label + "': driver does not support screenshots");
            return null;
        }

        try {
            Files.createDirectories(SCREENSHOT_DIR);
            TestLog.info("Capturing screenshot '" + label + "' | url=" + safeUrl(driver)
                    + " | title=" + safeTitle(driver));
            byte[] png = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            Path file = SCREENSHOT_DIR.resolve(FILE_TIME.format(LocalDateTime.now()) + "-" + sanitize(label) + ".png");
            Files.write(file, png);
            Allure.addAttachment(label, "image/png", new ByteArrayInputStream(png), "png");
            TestLog.info("Screenshot saved: " + file.toAbsolutePath());
            return file;
        } catch (IOException | RuntimeException e) {
            TestLog.error("Failed to capture screenshot '" + label + "' (browser may be unresponsive)", e);
            return null;
        }
    }

    private static String sanitize(String label) {
        String safe = label == null || label.isBlank() ? "screenshot" : label.trim();
        safe = safe.replaceAll("[^a-zA-Z0-9._-]+", "-");
        if (safe.length() > 80) {
            safe = safe.substring(0, 80);
        }
        return safe;
    }

    private static String safeUrl(WebDriver driver) {
        try {
            return driver.getCurrentUrl();
        } catch (RuntimeException e) {
            return "<unavailable>";
        }
    }

    private static String safeTitle(WebDriver driver) {
        try {
            return driver.getTitle();
        } catch (RuntimeException e) {
            return "<unavailable>";
        }
    }
}
