package com.saucedemo.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/** Reads test-config.properties: the only place credentials and users are defined. */
public final class TestConfig {

    private static final Properties PROPS = load();

    private TestConfig() {
    }

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = TestConfig.class.getClassLoader().getResourceAsStream("test-config.properties")) {
            if (in == null) {
                throw new IllegalStateException("test-config.properties not found on classpath");
            }
            p.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return p;
    }

    private static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null) {
            return sys;
        }
        String v = PROPS.getProperty(key);
        if (v == null) {
            throw new IllegalStateException("Missing config key: " + key);
        }
        return v.trim();
    }

    private static List<String> list(String key) {
        return Arrays.stream(get(key).split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public static String baseUrl() { return get("base.url"); }
    public static String browserBinary() { return get("browser.binary"); }
    public static String browserDriver() { return get("browser.driver"); }
    public static boolean headless() { return Boolean.parseBoolean(get("browser.headless")); }
    public static String password() { return get("password"); }
    public static List<String> users() { return list("users"); }
    public static String baselineUser() { return get("baseline.user"); }
    public static List<String> lockedOutUsers() { return list("known.lockedOutUsers"); }
    public static boolean isLockedOut(String user) { return lockedOutUsers().contains(user); }
    public static Duration waitTimeout() { return Duration.ofSeconds(Long.parseLong(get("wait.timeout.seconds"))); }
    public static long loginMaxMillis() { return Long.parseLong(get("perf.login.maxMillis")); }
    public static String firstName() { return get("checkout.firstName"); }
    public static String lastName() { return get("checkout.lastName"); }
    public static String postalCode() { return get("checkout.postalCode"); }
}
