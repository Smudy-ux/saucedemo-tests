# SauceDemo automated tests

Selenium + JUnit 5 tests for https://www.saucedemo.com/. Every test runs once per SauceDemo user and compares
the result against `standard_user`.

## Requirements

- **Java 21+** (`java -version`)
- **Maven 3.9+** (`mvn -v`)
- **Google Chrome** installed

chromedriver does **not** need to be installed: Selenium Manager downloads a matching one on the first run
(internet access required).

Open a **new** terminal so `mvn` is on the PATH.

## Running

From the project folder:

```powershell
mvn test
```

Run a single test class or method:

```powershell
mvn test -Dtest=LoginTest
mvn test -Dtest=InventoryTest#sortPriceLowToHigh
```

Run with a visible browser instead of headless:

```powershell
mvn test "-Dbrowser.headless=false"
```

Per-test reports are written to `target/surefire-reports`.

## Expected result: the build FAILS

SauceDemo has users that are broken on purpose, and this suite is meant to catch their bugs. A normal run
ends with something like:

```
Tests run: 56, Failures: 10, Errors: 0, Skipped: 9
```

| User | Expected outcome |
|---|---|
| `standard_user` | All pass (reference user) |
| `locked_out_user` | Cannot log in by design: its inventory/cart/checkout tests are **skipped** |
| `problem_user` | Fails: broken images, Remove button, checkout fields, sorting |
| `performance_glitch_user` | Fails: login takes ~5s (limit 3s) |
| `error_user` | Fails: sorting alert, Remove button, checkout last name |
| `visual_user` | Fails: wrong prices/images, broken price sort |

The `[1]`–`[6]` in test output are the users in the order above.

**Errors** (not Failures) such as `NoSuchDriver` or `SessionNotCreated` mean a setup problem: check that
Chrome is installed and that the machine can reach the internet.

## Configuration

All settings live in `src/test/resources/test-config.properties`. Any of them can be overridden on the
command line with `-Dkey=value`.

| Key | Default | Meaning |
|---|---|---|
| `base.url` | `https://www.saucedemo.com/` | Site under test |
| `browser.binary` | *(blank)* | Chrome/Chromium executable; blank = default Chrome install |
| `browser.driver` | *(blank)* | chromedriver path; blank = Selenium Manager downloads one |
| `browser.headless` | `true` | Run without a visible window |
| `users` | all six users | Which users the tests run for |
| `wait.timeout.seconds` | `15` | Explicit-wait timeout |
| `perf.login.maxMillis` | `3000` | Login time limit |
