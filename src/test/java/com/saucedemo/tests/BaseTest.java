package com.saucedemo.tests;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

import com.saucedemo.config.TestConfig;
import com.saucedemo.driver.DriverFactory;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

public abstract class BaseTest {

    /** Products used by the cart and checkout tests. */
    protected static final String ITEM_A = "Sauce Labs Backpack";
    protected static final String ITEM_B = "Sauce Labs Bike Light";

    protected WebDriver driver;

    /** Every configured user. Used as {@code @MethodSource("com.saucedemo.tests.BaseTest#users")}. */
    public static Stream<String> users() {
        return TestConfig.users().stream();
    }

    public static Stream<String> lockedOutUsers() {
        return TestConfig.lockedOutUsers().stream();
    }

    @BeforeEach
    void startBrowser() {
        driver = DriverFactory.create();
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Skips (does not pass) tests that need a logged-in session for users locked out by design. */
    protected static void skipIfLockedOut(String user) {
        assumeFalse(TestConfig.isLockedOut(user), "Known: " + user + " is locked out by design");
    }

    protected InventoryPage login(String user) {
        skipIfLockedOut(user);
        return new LoginPage(driver).open().loginAs(user);
    }
}
