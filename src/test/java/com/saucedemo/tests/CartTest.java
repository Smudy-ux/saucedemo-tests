package com.saucedemo.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CartPage.Line;
import com.saucedemo.pages.InventoryPage;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Cart")
class CartTest extends BaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Add two items: buttons, badge and cart contents")
    void addTwoItems(String user) {
        InventoryPage inventory = login(user);
        inventory.addToCart(ITEM_A);
        inventory.addToCart(ITEM_B);
        inventory.waitForCartBadge(2);
        assertEquals(2, inventory.cartBadgeCount(), "Cart badge");

        CartPage cart = inventory.openCart();
        assertEquals(List.of(
                        new Line(ITEM_A, Baseline.priceOf(ITEM_A)),
                        new Line(ITEM_B, Baseline.priceOf(ITEM_B))),
                cart.lines(), "Cart contents");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Remove one item on the cart page")
    void removeOnCartPage(String user) {
        InventoryPage inventory = login(user);
        inventory.addToCart(ITEM_A);
        inventory.addToCart(ITEM_B);
        inventory.waitForCartBadge(2);

        CartPage cart = inventory.openCart();
        cart.remove(ITEM_A);
        cart.waitForCartBadge(1);
        assertEquals(1, cart.cartBadgeCount(), "Cart badge after removal");
        assertEquals(List.of(ITEM_B), cart.itemNames(), "Cart contents after removal");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Remove an item on the inventory page")
    void removeOnInventoryPage(String user) {
        InventoryPage inventory = login(user);
        inventory.addToCart(ITEM_A);
        inventory.waitForCartBadge(1);

        inventory.removeFromCart(ITEM_A);
        assertEquals("Add to cart", inventory.buttonText(ITEM_A), "Button label after removal");
        inventory.waitForCartBadge(0);
        assertEquals(0, inventory.cartBadgeCount(), "Cart badge after removal");
    }
}
