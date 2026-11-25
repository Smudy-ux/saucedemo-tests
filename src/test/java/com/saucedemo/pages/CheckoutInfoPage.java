package com.saucedemo.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutInfoPage extends BasePage {

    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE = By.cssSelector("[data-test='continue']");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    public CheckoutInfoPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
        visible(FIRST_NAME);
    }

    public CheckoutInfoPage fill(String first, String last, String postal) {
        visible(FIRST_NAME).sendKeys(first);
        visible(LAST_NAME).sendKeys(last);
        visible(POSTAL_CODE).sendKeys(postal);
        return this;
    }

    public String firstName() { return valueOf(FIRST_NAME); }
    public String lastName() { return valueOf(LAST_NAME); }
    public String postalCode() { return valueOf(POSTAL_CODE); }

    /** Clicks Continue; fails with the on-page error if the form is rejected. */
    public CheckoutOverviewPage continueToOverview() {
        clickable(CONTINUE).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/checkout-step-two.html"),
                ExpectedConditions.visibilityOfElementLocated(ERROR)));
        List<WebElement> errors = driver.findElements(ERROR);
        if (!errors.isEmpty()) {
            throw new AssertionError("Checkout form rejected: \"" + errors.get(0).getText() + "\"");
        }
        return new CheckoutOverviewPage(driver);
    }
}
