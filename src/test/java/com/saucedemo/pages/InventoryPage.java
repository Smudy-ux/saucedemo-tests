package com.saucedemo.pages;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {

    public static final By INVENTORY_LIST = By.cssSelector("[data-test='inventory-list']");
    private static final By ITEM = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By ITEM_IMAGE = By.cssSelector("img.inventory_item_img");
    private static final By ITEM_BUTTON = By.cssSelector("button.btn_inventory");
    private static final By SORT = By.cssSelector("[data-test='product-sort-container']");
    private static final By LOGIN_ERROR = By.cssSelector("[data-test='error']");

    public enum Sort {
        NAME_A_TO_Z("az"), NAME_Z_TO_A("za"), PRICE_LOW_TO_HIGH("lohi"), PRICE_HIGH_TO_LOW("hilo");

        public final String value;

        Sort(String value) {
            this.value = value;
        }
    }

    public record Product(String name, BigDecimal price, String imageSrc) {
    }

    private InventoryPage(WebDriver driver) {
        super(driver);
    }

    /** Waits for the inventory list; if a login error appears instead, fails with its text. */
    public static InventoryPage awaitLoaded(WebDriver driver) {
        InventoryPage page = new InventoryPage(driver);
        page.wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(INVENTORY_LIST),
                ExpectedConditions.visibilityOfElementLocated(LOGIN_ERROR)));
        List<WebElement> errors = driver.findElements(LOGIN_ERROR);
        if (!errors.isEmpty()) {
            throw new AssertionError("Login rejected: \"" + errors.get(0).getText() + "\"");
        }
        return page;
    }

    public List<Product> products() {
        return allVisible(ITEM).stream()
                .map(item -> new Product(
                        item.findElement(ITEM_NAME).getText(),
                        money(item.findElement(ITEM_PRICE).getText()),
                        item.findElement(ITEM_IMAGE).getAttribute("src")))
                .toList();
    }

    public List<String> productNames() {
        return products().stream().map(Product::name).toList();
    }

    public List<BigDecimal> productPrices() {
        return products().stream().map(Product::price).toList();
    }

    private WebElement itemByName(String name) {
        return allVisible(ITEM).stream()
                .filter(i -> i.findElement(ITEM_NAME).getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Product not on inventory page: " + name));
    }

    public String buttonText(String productName) {
        return itemByName(productName).findElement(ITEM_BUTTON).getText();
    }

    /** Clicks the product's button and waits until its label becomes {@code expectedLabel}. */
    public void clickProductButton(String productName, String expectedLabel) {
        WebElement button = itemByName(productName).findElement(ITEM_BUTTON);
        button.click();
        try {
            wait.until(d -> expectedLabel.equals(itemByName(productName).findElement(ITEM_BUTTON).getText()));
        } catch (TimeoutException e) {
            throw new AssertionError("Button for '" + productName + "' did not change to '" + expectedLabel
                    + "'; it still reads '" + buttonText(productName) + "'");
        }
    }

    public void addToCart(String productName) {
        clickProductButton(productName, "Remove");
    }

    public void removeFromCart(String productName) {
        clickProductButton(productName, "Add to cart");
    }

    public String selectedSort() {
        return new Select(driver.findElement(SORT)).getFirstSelectedOption().getDomProperty("value");
    }

    /**
     * Selects a sort option. Fails if the app raises an alert, or if the dropdown does not
     * settle on the chosen option.
     */
    public void sortBy(Sort sort) {
        new Select(clickable(SORT)).selectByValue(sort.value);
        consumeAlertIfPresent().ifPresent(text -> {
            throw new AssertionError("Sorting by '" + sort.value + "' raised an alert: \"" + text + "\"");
        });
        try {
            wait.until(d -> sort.value.equals(selectedSort()));
        } catch (TimeoutException e) {
            throw new AssertionError("Sort dropdown did not stay on '" + sort.value + "'; it shows '"
                    + selectedSort() + "'");
        }
        visible(INVENTORY_LIST);
    }
}
