package com.homedepot.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Unable to find " + CONFIG_FILE + " on the classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private ConfigReader() {
    }

    public static String getBaseUrl() {
        return getRequiredProperty("base.url");
    }

    public static int getImplicitWaitSeconds() {
        return Integer.parseInt(getRequiredProperty("implicit.wait"));
    }

    public static int getExplicitWaitSeconds() {
        return Integer.parseInt(getRequiredProperty("explicit.wait"));
    }

    public static String getSearchTerm() {
        return getRequiredProperty("search.term");
    }

    public static int getPageLoadTimeoutSeconds() {
        return Integer.parseInt(getRequiredProperty("page.load.timeout"));
    }

    public static int getScriptTimeoutSeconds() {
        return Integer.parseInt(getRequiredProperty("script.timeout"));
    }

    public static int getTestTimeoutSeconds() {
        return Integer.parseInt(getRequiredProperty("test.timeout"));
    }

    public static long getTestTimeoutMillis() {
        return getTestTimeoutSeconds() * 1000L;
    }

    public static boolean isScreenshotOnAction() {
        return Boolean.parseBoolean(properties.getProperty("screenshot.on.action", "false"));
    }

    public static boolean isHeadless() {
        String headless = System.getProperty("headless");
        if (headless == null || headless.isBlank()) {
            headless = System.getenv("HEADLESS");
        }
        return headless != null && Boolean.parseBoolean(headless);
    }

    private static String getRequiredProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing or empty config property: " + key);
        }
        return value.trim();
    }
}
