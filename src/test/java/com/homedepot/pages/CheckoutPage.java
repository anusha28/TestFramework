package com.homedepot.pages;

import com.homedepot.core.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    private static final By GUEST_CHECKOUT_BUTTON = By.id("guest-checkout-button");
    private static final By GUEST_CHECKOUT_ALT = By.id("continue-as-guest");
    private static final By GUEST_CHECKOUT_BY_NAME = By.name("guestCheckout");
    private static final By SIGN_IN_EMAIL = By.name("email");
    private static final By SIGN_IN_EMAIL_BY_ID = By.id("email");
    private static final By SIGN_IN_PASSWORD = By.name("password");
    private static final By CHECKOUT_HEADING = By.id("checkout-heading");
    private static final By CHECKOUT_FORM = By.id("checkout-form");
    private static final By CHECKOUT_FORM_BY_NAME = By.name("checkout");

    public CheckoutPage(WebDriver driver, WaitUtils waitUtils) {
        super(driver, waitUtils);
    }

    @Step("Verify guest checkout option is visible")
    public boolean isGuestCheckoutVisible() {
        return isAnyDisplayed(
                GUEST_CHECKOUT_BUTTON,
                GUEST_CHECKOUT_ALT,
                GUEST_CHECKOUT_BY_NAME,
                SIGN_IN_EMAIL,
                SIGN_IN_EMAIL_BY_ID,
                SIGN_IN_PASSWORD,
                CHECKOUT_FORM,
                CHECKOUT_FORM_BY_NAME
        );
    }

    @Step("Get checkout page title")
    public String getCheckoutPageTitle() {
        if (isDisplayed(CHECKOUT_HEADING)) {
            return getText(CHECKOUT_HEADING);
        }
        return driver.getTitle();
    }
}
