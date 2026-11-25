package com.saucedemo.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.saucedemo.config.TestConfig;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("performance")
@DisplayName("Login performance (<=3s)")
class LoginPerformanceTest extends BaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.saucedemo.tests.BaseTest#users")
    @DisplayName("Login click -> product list visible within limit")
    void loginWithinLimit(String user) {
        skipIfLockedOut(user);
        LoginPage page = new LoginPage(driver).open();

        long start = System.nanoTime();
        page.submit(user);
        InventoryPage.awaitLoaded(driver);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        long limit = TestConfig.loginMaxMillis();
        System.out.printf("[perf] %s login: %d ms (limit %d ms)%n", user, elapsedMs, limit);
        assertTrue(elapsedMs <= limit, String.format(
                "PERFORMANCE DEFECT: %s login took %.2fs (limit %.2fs)", user, elapsedMs / 1000.0, limit / 1000.0));
    }
}
