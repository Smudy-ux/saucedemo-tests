package com.saucedemo.tests;

import com.saucedemo.config.TestConfig;
import com.saucedemo.driver.DriverFactory;
import com.saucedemo.pages.InventoryPage.Product;
import com.saucedemo.pages.LoginPage;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.WebDriver;

/** Inventory as seen by the baseline user (standard_user); captured once per run in its own browser. */
final class Baseline {

    private static List<Product> products;

    private Baseline() {
    }

    static synchronized List<Product> products() {
        if (products == null) {
            WebDriver driver = DriverFactory.create();
            try {
                products = new LoginPage(driver).open().loginAs(TestConfig.baselineUser()).products();
            } finally {
                driver.quit();
            }
        }
        return products;
    }

    static BigDecimal priceOf(String name) {
        return products().stream()
                .filter(p -> p.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Baseline has no product " + name))
                .price();
    }
}
