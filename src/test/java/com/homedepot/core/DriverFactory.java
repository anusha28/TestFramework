package com.homedepot.core;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;

import java.time.Duration;
import java.util.Map;

public final class DriverFactory {

    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=" + WINDOW_WIDTH + "," + WINDOW_HEIGHT);
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        // Return from driver.get() as soon as navigation starts; do not wait for images/scripts.
        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        options.setExperimentalOption("prefs", Map.of(
                "profile.managed_default_content_settings.images", 2
        ));

        WebDriver chrome = new ChromeDriver(options);
        chrome.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWaitSeconds()))
                .pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeoutSeconds()))
                .scriptTimeout(Duration.ofSeconds(ConfigReader.getScriptTimeoutSeconds()));

        WebDriver driver = new EventFiringDecorator<>(new DriverEventLogger()).decorate(chrome);

        if (!ConfigReader.isHeadless()) {
            driver.manage().window().maximize();
        } else {
            driver.manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        }

        return driver;
    }
}
