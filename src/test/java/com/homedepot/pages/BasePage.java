package com.homedepot.pages;

import com.homedepot.core.ConfigReader;
import com.homedepot.core.ScreenshotUtils;
import com.homedepot.core.TestLog;
import com.homedepot.core.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitUtils waitUtils;

    protected BasePage(WebDriver driver, WaitUtils waitUtils) {
        this.driver = driver;
        this.waitUtils = waitUtils;
    }

    protected void click(By locator) {
        TestLog.info("Click " + locator);
        waitUtils.waitForClickable(locator).click();
        captureAction("click");
    }

    protected void type(By locator, String text) {
        TestLog.info("Type into " + locator + ": " + text);
        WebElement element = waitUtils.waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        captureAction("type");
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected String getText(By locator) {
        return waitUtils.waitForVisible(locator).getText();
    }

    protected void clickIfDisplayed(By locator) {
        if (isDisplayed(locator)) {
            TestLog.info("Optional element shown, clicking " + locator);
            click(locator);
        } else {
            TestLog.info("Optional element not shown, skipping " + locator);
        }
    }

    protected void clickFirstDisplayed(By... locators) {
        for (By locator : locators) {
            if (isDisplayed(locator)) {
                click(locator);
                return;
            }
        }
        TestLog.error("None of the locators were visible: " + java.util.Arrays.toString(locators));
        ScreenshotUtils.capture(driver, "no-locator-visible");
        throw new IllegalStateException("None of the locators were visible: " + java.util.Arrays.toString(locators));
    }

    protected boolean isAnyDisplayed(By... locators) {
        for (By locator : locators) {
            if (isDisplayed(locator)) {
                return true;
            }
        }
        return false;
    }

    private void captureAction(String action) {
        if (ConfigReader.isScreenshotOnAction()) {
            ScreenshotUtils.capture(driver, action);
        }
    }
}
