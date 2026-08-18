package com.homedepot.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

public class DriverEventLogger implements WebDriverListener {

    @Override
    public void beforeGet(WebDriver driver, String url) {
        TestLog.info("Navigate: " + url);
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
        TestLog.info("Page loaded: url=" + safeUrl(driver) + " | title=" + safeTitle(driver));
    }

    @Override
    public void beforeClick(WebElement element) {
        TestLog.info("Click: " + describe(element));
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        TestLog.info("Type into " + describe(element) + ": " + String.join("", keysToSend));
    }

    private static String describe(WebElement element) {
        try {
            String tag = element.getTagName();
            String id = element.getDomAttribute("id");
            String name = element.getDomAttribute("name");
            StringBuilder description = new StringBuilder(tag);
            if (id != null && !id.isBlank()) {
                description.append('#').append(id);
            }
            if (name != null && !name.isBlank()) {
                description.append("[name=").append(name).append(']');
            }
            return description.toString();
        } catch (RuntimeException e) {
            return "<stale or unknown element>";
        }
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
