package com.saucedemo.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.InventoryPage.Product;
import com.saucedemo.pages.InventoryPage.Sort;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Inventory")
class InventoryTest extends BaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Product names, prices and images match standard_user")
    void productsMatchBaseline(String user) {
        List<Product> actual = login(user).products();
        List<Product> expected = Baseline.products();

        assertAll(
                () -> assertEquals(names(expected), names(actual), "Product names differ from standard_user"),
                () -> assertEquals(expected.stream().map(Product::price).toList(),
                        actual.stream().map(Product::price).toList(), "Product prices differ from standard_user"),
                () -> assertEquals(expected.stream().map(Product::imageSrc).toList(),
                        actual.stream().map(Product::imageSrc).toList(), "Product images differ from standard_user"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Sort by Name (A to Z)")
    void sortNameAToZ(String user) {
        InventoryPage inventory = login(user);
        inventory.sortBy(Sort.NAME_A_TO_Z);

        List<String> names = inventory.productNames();
        assertEquals(names.stream().sorted().toList(), names, "Names are not in A-Z order");
        assertEquals(names(Baseline.products()).stream().sorted().toList(), names,
                "Sorted A-Z list differs from standard_user's products");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Sort by Price (low to high)")
    void sortPriceLowToHigh(String user) {
        InventoryPage inventory = login(user);
        inventory.sortBy(Sort.PRICE_LOW_TO_HIGH);

        List<BigDecimal> prices = inventory.productPrices();
        assertEquals(prices.stream().sorted(Comparator.naturalOrder()).toList(), prices,
                "Prices are not in low-to-high order; products shown: " + inventory.productNames());
    }

    private static List<String> names(List<Product> products) {
        return products.stream().map(Product::name).toList();
    }
}
