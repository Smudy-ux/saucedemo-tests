package com.saucedemo.pages;

import com.saucedemo.config.TestConfig;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.cssSelector("[data-test='username']");
    private static final By PASSWORD = By.cssSelector("[data-test='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-test='login-button']");
    private static final By ERROR = By.cssSelector("[data-test='error']");
    private static final By USERNAMES_PANEL = By.cssSelector("[data-test='login-credentials']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(TestConfig.baseUrl());
        visible(LOGIN_BUTTON);
        return this;
    }

    /** Usernames listed under "Accepted usernames are:". */
    public List<String> acceptedUsernames() {
        String text = visible(USERNAMES_PANEL).getText();
        return Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.endsWith(":"))
                .toList();
    }

    public void submit(String username) {
        visible(USERNAME).sendKeys(username);
        driver.findElement(PASSWORD).sendKeys(TestConfig.password());
        clickable(LOGIN_BUTTON).click();
    }

    /** Logs in and waits for the inventory page. Fails with the on-page error if login is rejected. */
    public InventoryPage loginAs(String username) {
        submit(username);
        return InventoryPage.awaitLoaded(driver);
    }

    public String waitForError() {
        return visible(ERROR).getText();
    }
}
