package com.saucedemo.pages;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends BasePage {

    private static final By CART_LIST = By.cssSelector("[data-test='cart-list']");
    private static final By ITEM = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By REMOVE_BUTTON = By.cssSelector("button.cart_button");
    private static final By CHECKOUT = By.cssSelector("[data-test='checkout']");

    public record Line(String name, BigDecimal price) {
    }

    public CartPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("/cart.html"));
        visible(CART_LIST);
    }

    public List<Line> lines() {
        return driver.findElements(ITEM).stream()
                .map(i -> new Line(i.findElement(ITEM_NAME).getText(), money(i.findElement(ITEM_PRICE).getText())))
                .toList();
    }

    public List<String> itemNames() {
        return lines().stream().map(Line::name).toList();
    }

    public void remove(String productName) {
        WebElement item = driver.findElements(ITEM).stream()
                .filter(i -> i.findElement(ITEM_NAME).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Product not in cart: " + productName));
        item.findElement(REMOVE_BUTTON).click();
        try {
            wait.until(d -> !itemNames().contains(productName));
        } catch (TimeoutException e) {
            throw new AssertionError("'" + productName + "' was still in the cart after clicking Remove");
        }
    }

    public CheckoutInfoPage checkout() {
        clickable(CHECKOUT).click();
        return new CheckoutInfoPage(driver);
    }
}
