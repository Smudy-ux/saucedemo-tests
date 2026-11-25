package com.saucedemo.pages;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutOverviewPage extends BasePage {

    private static final By ITEM = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By SUBTOTAL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL = By.cssSelector("[data-test='total-label']");
    private static final By FINISH = By.cssSelector("[data-test='finish']");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("/checkout-step-two.html"));
        visible(TOTAL);
    }

    public List<CartPage.Line> lines() {
        return driver.findElements(ITEM).stream()
                .map(i -> new CartPage.Line(i.findElement(ITEM_NAME).getText(),
                        money(i.findElement(ITEM_PRICE).getText())))
                .toList();
    }

    public BigDecimal itemTotal() { return money(visible(SUBTOTAL).getText()); }
    public BigDecimal tax() { return money(visible(TAX).getText()); }
    public BigDecimal total() { return money(visible(TOTAL).getText()); }

    /** Clicks Finish; fails if the order-complete page does not appear. */
    public CheckoutCompletePage finish() {
        clickable(FINISH).click();
        consumeAlertIfPresent().ifPresent(text -> {
            throw new AssertionError("Finish raised an alert: \"" + text + "\"");
        });
        try {
            wait.until(ExpectedConditions.urlContains("/checkout-complete.html"));
        } catch (TimeoutException e) {
            throw new AssertionError("Clicking Finish did not reach the order-complete page; still on "
                    + driver.getCurrentUrl());
        }
        return new CheckoutCompletePage(driver);
    }
}
