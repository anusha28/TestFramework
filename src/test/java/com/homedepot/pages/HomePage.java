package com.homedepot.pages;

import com.homedepot.core.ConfigReader;
import com.homedepot.core.TestLog;
import com.homedepot.core.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ScriptTimeoutException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class HomePage extends BasePage {

    private static final By COOKIE_ACCEPT_BUTTON = By.id("onetrust-accept-btn-handler");
    private static final By SEARCH_INPUT = By.id("typeahead-search-field-input");
    private static final By STORE_MODAL_CLOSE = By.id("store-closer");

    public HomePage(WebDriver driver, WaitUtils waitUtils) {
        super(driver, waitUtils);
    }

    @Step("Accept cookies if consent banner is displayed")
    public HomePage acceptCookiesIfPresent() {
        clickIfDisplayed(COOKIE_ACCEPT_BUTTON);
        return this;
    }

    @Step("Dismiss store selection prompt if displayed")
    public HomePage dismissStorePromptIfPresent() {
        clickIfDisplayed(STORE_MODAL_CLOSE);
        return this;
    }

    @Step("Search for product: {term}")
    public SearchResultsPage search(String term) {
        TestLog.info("Find search bar, insert '" + term + "', go to results");
        waitUtils.waitForPresence(SEARCH_INPUT);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));
        try {
            // Do not form.submit() here: navigation unloads the page and executeScript never returns.
            js.executeScript(
                    "const term = arguments[0];"
                            + "const input = document.getElementById('typeahead-search-field-input')"
                            + "  || document.querySelector('input[name=\"keyword\"]');"
                            + "if (!input) { return; }"
                            + "const setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;"
                            + "if (setter) { setter.call(input, term); } else { input.value = term; }"
                            + "input.dispatchEvent(new Event('input', { bubbles: true }));"
                            + "const button = document.getElementById('typeahead-search-icon-button');"
                            + "if (button) { button.click(); }",
                    term);
            TestLog.info("Inserted '" + term + "' and clicked search");
        } catch (ScriptTimeoutException e) {
            TestLog.info("Search script timed out; checking whether results already loaded");
        } finally {
            driver.manage().timeouts().scriptTimeout(
                    Duration.ofSeconds(ConfigReader.getScriptTimeoutSeconds()));
        }

        if (!isOnSearchResults()) {
            String resultsUrl = ConfigReader.getBaseUrl().replaceAll("/$", "") + "/s/" + term;
            TestLog.info("Opening search results: " + resultsUrl);
            driver.navigate().to(resultsUrl);
        }

        try {
            waitUtils.waitForUrlContainsAny("/s/", "/b/");
        } catch (TimeoutException e) {
            String resultsUrl = ConfigReader.getBaseUrl().replaceAll("/$", "") + "/s/" + term;
            TestLog.info("URL did not change; opening " + resultsUrl);
            driver.navigate().to(resultsUrl);
            waitUtils.waitForUrlContainsAny("/s/", "/b/");
        }
        return new SearchResultsPage(driver, waitUtils);
    }

    private boolean isOnSearchResults() {
        try {
            String url = driver.getCurrentUrl();
            if (url == null) {
                return false;
            }
            String lower = url.toLowerCase();
            return lower.contains("/s/") || lower.contains("/b/");
        } catch (RuntimeException e) {
            return false;
        }
    }
}
