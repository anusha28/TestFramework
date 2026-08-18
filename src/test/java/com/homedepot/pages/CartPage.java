package com.homedepot.pages;

import com.homedepot.core.WaitUtils;
import com.homedepot.core.TestLog;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    private static final By CART_ITEMS = By.id("cart-items");
    private static final By CART_ITEM_BY_NAME = By.name("cartItem");
    private static final By CHECKOUT_BUTTON = By.id("checkout-button");
    private static final By CHECKOUT_BUTTON_ALT = By.id("checkout");
    private static final By CHECKOUT_BY_NAME = By.name("checkout");
    private static final By CHECKOUT_TAB = By.xpath(
            "//*[self::a or self::button or self::span][contains(translate(normalize-space(.),'CHECKOUT','checkout'),'checkout')]"
    );
    private static final By EMPTY_CART_MESSAGE = By.id("empty-cart");

    public CartPage(WebDriver driver, WaitUtils waitUtils) {
        super(driver, waitUtils);
    }

    public boolean hasItems() {
        if (isDisplayed(EMPTY_CART_MESSAGE)) {
            return false;
        }
        return isPresent(CART_ITEMS) || isPresent(CART_ITEM_BY_NAME);
    }

    @Step("Verify checkout tab or button is present")
    public boolean isCheckoutPresent() {
        waitUtils.waitForAnyVisible(CHECKOUT_BUTTON, CHECKOUT_BUTTON_ALT, CHECKOUT_BY_NAME, CHECKOUT_TAB);
        TestLog.info("Checkout control is present");
        return true;
    }

    @Step("Proceed to checkout from cart")
    public CheckoutPage proceedToCheckout() {
        if (!hasItems()) {
            throw new IllegalStateException("Cannot proceed to checkout with an empty cart");
        }

        clickFirstDisplayed(CHECKOUT_BUTTON, CHECKOUT_BUTTON_ALT, CHECKOUT_BY_NAME);
        waitUtils.waitForUrlContains("checkout");
        return new CheckoutPage(driver, waitUtils);
    }
}
