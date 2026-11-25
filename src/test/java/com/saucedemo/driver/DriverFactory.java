package com.saucedemo.driver;

import com.saucedemo.config.TestConfig;
import java.io.File;
import java.util.Map;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

/** Creates a Chromium driver using the locally installed binary and chromedriver (no Selenium Manager downloads). */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary(TestConfig.browserBinary());
        if (TestConfig.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080", "--disable-extensions", "--no-first-run");
        // Keep Chromium's password manager / leak-detection bubbles out of the way.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        // If the app raises a JS alert we did not expect, the next command fails with its text
        // instead of hanging.
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT_AND_NOTIFY);

        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(TestConfig.browserDriver()))
                .build();
        return new ChromeDriver(service, options);
    }
}
