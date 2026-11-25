package com.saucedemo.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Login")
class LoginTest extends BaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Login lands on inventory with 6 products")
    void loginSucceeds(String user) {
        long start = System.nanoTime();
        InventoryPage inventory = login(user);
        System.out.printf("[timing] %s login + inventory load: %d ms%n", user, (System.nanoTime() - start) / 1_000_000);

        assertTrue(inventory.currentUrl().endsWith("/inventory.html"),
                "Expected /inventory.html but was " + inventory.currentUrl());
        assertEquals(6, inventory.products().size(), "Product count on inventory page");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#lockedOutUsers")
    @DisplayName("Locked-out user is rejected with the locked-out error")
    void lockedOutUserIsRejected(String user) {
        LoginPage page = new LoginPage(driver).open();
        page.submit(user);
        assertEquals("Epic sadface: Sorry, this user has been locked out.", page.waitForError());
        assertTrue(page.currentUrl().endsWith("saucedemo.com/"), "Should stay on login page, was " + page.currentUrl());
    }
}
