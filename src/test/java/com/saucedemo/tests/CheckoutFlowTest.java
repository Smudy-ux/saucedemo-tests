package com.saucedemo.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.saucedemo.config.TestConfig;
import com.saucedemo.pages.CartPage.Line;
import com.saucedemo.pages.CheckoutInfoPage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.InventoryPage;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Checkout end-to-end")
class CheckoutFlowTest extends BaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Login -> add 2 items -> checkout -> finish")
    void fullCheckout(String user) {
        List<Line> expectedLines = List.of(
                new Line(ITEM_A, Baseline.priceOf(ITEM_A)),
                new Line(ITEM_B, Baseline.priceOf(ITEM_B)));

        InventoryPage inventory = login(user);
        inventory.addToCart(ITEM_A);
        inventory.addToCart(ITEM_B);
        inventory.waitForCartBadge(2);

        CheckoutInfoPage info = inventory.openCart().checkout()
                .fill(TestConfig.firstName(), TestConfig.lastName(), TestConfig.postalCode());
        assertAll("[checkout info] typed values must stick",
                () -> assertEquals(TestConfig.firstName(), info.firstName(), "First name field"),
                () -> assertEquals(TestConfig.lastName(), info.lastName(), "Last name field"),
                () -> assertEquals(TestConfig.postalCode(), info.postalCode(), "Postal code field"));

        CheckoutOverviewPage overview = info.continueToOverview();
        BigDecimal sum = overview.lines().stream().map(Line::price).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertAll("[overview]",
                () -> assertEquals(expectedLines, overview.lines(), "Items on overview"),
                () -> assertEquals(sum, overview.itemTotal(), "Item total vs sum of item prices"),
                () -> assertEquals(overview.itemTotal().add(overview.tax()), overview.total(),
                        "Item total + tax vs total"));

        assertEquals("Thank you for your order!", overview.finish().header(), "[complete] header");
    }
}
