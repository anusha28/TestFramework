package com.homedepot.core;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class WaitUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final int timeoutSeconds;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.timeoutSeconds = ConfigReader.getExplicitWaitSeconds();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    public WebElement waitForVisible(By locator) {
        return until("visible " + locator, ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForAnyVisible(By... locators) {
        return until("any present " + Arrays.toString(locators), driver -> {
            for (By locator : locators) {
                List<WebElement> elements = driver.findElements(locator);
                if (!elements.isEmpty()) {
                    return elements.get(0);
                }
            }
            return null;
        });
    }

    public WebElement waitForClickable(By locator) {
        return until("clickable " + locator, ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForClickable(WebElement element) {
        return until("clickable element", ExpectedConditions.elementToBeClickable(element));
    }

    public boolean waitForInvisible(By locator) {
        return until("invisible " + locator, ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean waitForTitleContains(String text) {
        String expected = text.toLowerCase();
        return until("title contains '" + text + "'", driver -> {
            String title = driver.getTitle();
            return title != null && title.toLowerCase().contains(expected);
        });
    }

    public boolean waitForUrlContains(String urlFragment) {
        return waitForUrlContainsAny(urlFragment);
    }

    public boolean waitForUrlContainsAny(String... urlFragments) {
        return until("URL contains any of " + Arrays.toString(urlFragments), driver -> {
            String url = driver.getCurrentUrl();
            if (url == null) {
                return false;
            }
            String lower = url.toLowerCase();
            for (String fragment : urlFragments) {
                if (lower.contains(fragment.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
    }

    public List<WebElement> waitForAllVisible(By locator) {
        return until("all visible " + locator, ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement waitForPresence(By locator) {
        return until("present " + locator, ExpectedConditions.presenceOfElementLocated(locator));
    }

    public <T> T waitFor(ExpectedCondition<T> condition) {
        return until(String.valueOf(condition), condition);
    }

    public WebDriver getDriver() {
        return driver;
    }

    private <T> T until(String description, ExpectedCondition<T> condition) {
        TestLog.info("Waiting up to " + timeoutSeconds + "s for " + description);
        try {
            T result = wait.until(condition);
            TestLog.info("Wait passed: " + description);
            return result;
        } catch (TimeoutException e) {
            TestLog.error("Timed out after " + timeoutSeconds + "s waiting for " + description
                    + " | url=" + safeUrl()
                    + " | title=" + safeTitle());
            throw e;
        }
    }

    private String safeUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (RuntimeException e) {
            return "<unavailable>";
        }
    }

    private String safeTitle() {
        try {
            return driver.getTitle();
        } catch (RuntimeException e) {
            return "<unavailable>";
        }
    }
}
