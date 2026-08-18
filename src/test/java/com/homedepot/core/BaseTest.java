package com.homedepot.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    /** Mirrors test.timeout in config.properties / testng.xml (milliseconds). */
    protected static final long TEST_TIMEOUT_MS = 180_000L;

    protected WebDriver driver;
    protected WaitUtils waitUtils;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        TestLog.info("Timeouts: pageLoad=" + ConfigReader.getPageLoadTimeoutSeconds()
                + "s, action(explicit)=" + ConfigReader.getExplicitWaitSeconds()
                + "s, implicit=" + ConfigReader.getImplicitWaitSeconds()
                + "s, test=" + ConfigReader.getTestTimeoutSeconds() + "s");

        driver = DriverFactory.createDriver();
        waitUtils = new WaitUtils(driver);

        try {
            driver.get(ConfigReader.getBaseUrl());
        } catch (WebDriverException e) {
            String url = safeUrl();
            String title = safeTitle();
            TestLog.error("Initial navigation did not complete cleanly"
                    + " | url=" + url
                    + " | title=" + title);

            if (!isTargetSiteAvailable(url, title)) {
                ScreenshotUtils.capture(driver, "failed-initial-load");
                throw e;
            }
            TestLog.info("Continuing because the Home Depot page is already available");
        }

        waitUtils.waitForTitleContains("Home Depot");
        TestLog.info("Title is visible; skipping full page/image load and moving to search");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver == null) {
            return;
        }

        String status = switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "UNKNOWN";
        };
        TestLog.info("Test " + result.getName() + " " + status);

        if (result.getStatus() == ITestResult.FAILURE) {
            TestLog.error("Failure at url=" + safeUrl() + " | title=" + safeTitle(), result.getThrowable());
            ScreenshotUtils.capture(driver, "failure-" + result.getName());
        }

        driver.quit();
        driver = null;
        waitUtils = null;
    }

    private boolean isTargetSiteAvailable(String url, String title) {
        String safeUrl = url == null ? "" : url.toLowerCase();
        String safeTitle = title == null ? "" : title.toLowerCase();
        return safeUrl.contains("homedepot.com") || safeTitle.contains("home depot");
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
