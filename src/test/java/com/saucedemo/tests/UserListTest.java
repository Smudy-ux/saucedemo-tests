package com.saucedemo.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.saucedemo.config.TestConfig;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserListTest extends BaseTest {

    @Test
    @DisplayName("Configured users match the login page's accepted usernames")
    void configuredUsersMatchLoginPage() {
        var onPage = new LoginPage(driver).open().acceptedUsernames();
        assertEquals(onPage, TestConfig.users(),
                "test-config.properties 'users' is out of sync with the login page");
    }
}
