package com.saucedemo.pages;

import com.saucedemo.config.TestConfig;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Shared helpers for all page objects. Explicit waits only. */
public abstract class BasePage {

    protected static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    protected static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TestConfig.waitTimeout());
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /** Text the user typed into an input, as the browser holds it. */
    protected String valueOf(By locator) {
        return driver.findElement(locator).getDomProperty("value");
    }

    /**
     * Returns and accepts a JS alert if one is open right now. Alerts raised by synchronous
     * event handlers are already open when the triggering command returns, so no wait is needed.
     */
    public Optional<String> consumeAlertIfPresent() {
        try {
            Alert alert = driver.switchTo().alert();
            String text = alert.getText();
            alert.accept();
            return Optional.of(text);
        } catch (NoAlertPresentException e) {
            return Optional.empty();
        }
    }

    /** Cart badge count; 0 when the badge is absent. */
    public int cartBadgeCount() {
        List<WebElement> badges = driver.findElements(CART_BADGE);
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText().trim());
    }

    public void waitForCartBadge(int expected) {
        if (expected == 0) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(CART_BADGE));
        } else {
            wait.until(ExpectedConditions.textToBe(CART_BADGE, String.valueOf(expected)));
        }
    }

    public CartPage openCart() {
        clickable(CART_LINK).click();
        return new CartPage(driver);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    /** "$29.99" or "Item total: $39.98" -> 29.99 / 39.98 */
    protected static BigDecimal money(String text) {
        int i = text.indexOf('$');
        return new BigDecimal(text.substring(i + 1).trim());
    }
}
