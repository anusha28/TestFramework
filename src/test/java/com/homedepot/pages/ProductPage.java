package com.homedepot.pages;

import com.homedepot.core.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductPage extends BasePage {

    private static final By ADD_TO_CART_BY_ID = By.id("add-to-cart-button");
    private static final By ADD_TO_CART_ALT_ID = By.id("addToCart");
    private static final By ADD_TO_CART_BY_NAME = By.name("addToCart");
    private static final By CART_ICON = By.id("headerCart");
    private static final By CART_ICON_ALT = By.id("cart");
    private static final By CART_ITEM_COUNT = By.id("headerCartCount");
    private static final By CART_ITEM_COUNT_ALT = By.id("cartCount");
    private static final By CONFIRMATION_CLOSE = By.id("atc-close");

    public ProductPage(WebDriver driver, WaitUtils waitUtils) {
        super(driver, waitUtils);
    }

    @Step("Add product to cart")
    public ProductPage addToCart() {
        clickFirstDisplayed(ADD_TO_CART_BY_ID, ADD_TO_CART_ALT_ID, ADD_TO_CART_BY_NAME);
        waitForAddToCartConfirmation();
        return this;
    }

    @Step("Navigate to cart from product page")
    public CartPage goToCart() {
        clickFirstDisplayed(CART_ICON, CART_ICON_ALT);
        waitUtils.waitForUrlContains("/cart");
        return new CartPage(driver, waitUtils);
    }

    public boolean isItemInCart() {
        By countLocator = firstPresent(CART_ITEM_COUNT, CART_ITEM_COUNT_ALT);
        if (countLocator == null) {
            return false;
        }
        String countText = driver.findElement(countLocator).getText().trim();
        return !countText.isEmpty() && !countText.equals("0");
    }

    private void waitForAddToCartConfirmation() {
        clickIfDisplayed(CONFIRMATION_CLOSE);
        if (isItemInCart()) {
            return;
        }
        waitUtils.waitFor(driver -> isItemInCart());
    }

    private By firstPresent(By... locators) {
        for (By locator : locators) {
            if (isPresent(locator)) {
                return locator;
            }
        }
        return null;
    }
}
