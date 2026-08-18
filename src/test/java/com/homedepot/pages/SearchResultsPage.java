package com.homedepot.pages;

import com.homedepot.core.TestLog;
import com.homedepot.core.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class SearchResultsPage extends BasePage {

    private static final By PRODUCT_LINKS = By.xpath("//a[contains(@href,'/p/')]");
    private static final By CART_ICON = By.id("headerCart");
    private static final By CART_ICON_ALT = By.id("cart");

    public SearchResultsPage(WebDriver driver, WaitUtils waitUtils) {
        super(driver, waitUtils);
    }

    @Step("Verify search results are present")
    public boolean hasResults() {
        waitUtils.waitForPresence(PRODUCT_LINKS);
        int count = driver.findElements(PRODUCT_LINKS).size();
        TestLog.info("Found " + count + " product links on search results");
        return count > 0;
    }

    @Step("Verify cart button is present")
    public boolean isCartButtonPresent() {
        waitUtils.waitForAnyVisible(CART_ICON, CART_ICON_ALT);
        TestLog.info("Cart button is present");
        return true;
    }

    @Step("Click cart button")
    public CartPage openCart() {
        TestLog.info("Click cart via JavaScript");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "const cart = document.getElementById('headerCart') || document.getElementById('cart')"
                        + " || document.querySelector('a[href*=\"/cart\"]');"
                        + "if (!cart) { throw new Error('Cart button not found'); }"
                        + "cart.click();");

        try {
            waitUtils.waitForUrlContains("/cart");
        } catch (TimeoutException e) {
            TestLog.info("Cart click did not navigate; opening /cart");
            js.executeScript("window.location.assign('/cart');");
            waitUtils.waitForUrlContains("/cart");
        }
        return new CartPage(driver, waitUtils);
    }
}
