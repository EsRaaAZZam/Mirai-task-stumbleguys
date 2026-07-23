# Stumble Guys Web Automation Framework

End-to-end automation for the **Stumble Guys web portal** ([stumbleguys.com](https://www.stumbleguys.com/)).

Covers:
- **Login flow** — desktop web (Chrome / Firefox / Edge)
- **Purchase flow** — stop before payment confirmation
- **WebGL game** — canvas load & interaction (bonus)
- **Mobile web** — Android Chrome via Appium (Samsung Galaxy A55, Android 16)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Web automation | Selenium 4.21 |
| Mobile automation | Appium 3.4.2 + UiAutomator2 (Android Chrome) |
| Test runner | TestNG 7.10.2 |
| Reporting | Allure 2 |
| Config | Owner library |
| Build | Maven |

---

## Project Structure

```
stumble-guys-automation/
├── pom.xml
├── src/
│   ├── main/java/com/stumbleguys/
│   │   ├── config/            # Owner-based config interface + manager
│   │   ├── driver/            # DriverManager (ThreadLocal), BrowserFactory, TargetFactory
│   │   ├── mobile/            # MobileDriverManager (Android Chrome via Appium)
│   │   ├── elementActions/    # Element.java — all Selenium actions
│   │   ├── pages/             # Page Object classes
│   │   │   ├── LandingPage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── StorePage.java
│   │   │   ├── CheckoutPage.java
│   │   │   └── GamePage.java (WebGL)
│   │   └── report/            # AllureManager, AllureUtils
│   └── test/
│       ├── java/com/stumbleguys/
│       │   ├── tests/
│       │   │   ├── BaseTest.java          # Web base (setup/teardown)
│       │   │   ├── web/
│       │   │   │   ├── LoginTest.java
│       │   │   │   ├── PurchaseFlowTest.java
│       │   │   │   └── WebGLGameTest.java
│       │   │   └── mobile/
│       │   │       ├── MobileBaseTest.java
│       │   │       ├── MobileLoginTest.java
│       │   │       └── MobilePurchaseFlowTest.java
│       │   └── listeners/
│       │       └── RetryAnalyzer.java
│       └── resources/
│           ├── stumbleguys.properties   # All configuration
│           ├── allure.properties
│           └── suites/
│               ├── web-suite.xml
│               └── mobile-suite.xml
```

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 17+ |
| Maven | 3.8+ |
| Chrome | Latest |
| ChromeDriver | Auto-managed by Selenium Manager |
| Appium Server | 2.x+ (for mobile tests only) |
| uiautomator2 driver | `appium driver install uiautomator2` |
| Android SDK + adb | For mobile tests only |
| Allure CLI | 2.x (for report generation) |

---

## Setup

### 1. Clone the repository
```bash
git clone https://github.com/EsRaaAZZam/stumble-guys-automation.git
cd stumble-guys-automation
```

### 2. Configure properties
Edit `src/test/resources/stumbleguys.properties`:

```properties
# Main site
url       = https://www.stumbleguys.com/
store.url = https://www.stumbleguys.com/shop
game.url  = https://www.stumbleguys.com/play

# Browser: chrome | firefox | edge
browser  = chrome
headless = false

# Android (mobile tests only — update to match your device)
appium.serverUrl        = http://127.0.0.1:4723
android.deviceName      = SM-A556E
android.udid            = R5CX80VG0AW   # run: adb devices
android.platformVersion = 16
```

For mobile, install the ChromeDriver matching your device's Chrome version and point to it:
```properties
# not required — auto-detected via capabilities in MobileDriverManager
```

---

## Running Tests

### Desktop Web Tests
```bash
# Run all web tests (login + purchase + game)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/web-suite.xml

# Run headless
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/web-suite.xml -Dheadless=true
```

### Mobile Tests (Android Chrome via Appium)
1. Start Appium server: `appium`
2. Connect Android device: `adb devices`
3. Verify device UDID matches `android.udid` in properties

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/mobile-suite.xml
```

### Generate Allure Report
```bash
allure serve target/allure-results
```

---

## Test Cases

### Login Feature — Web (Chrome/Firefox/Edge)
| Test | Description |
|---|---|
| `testLandingPageLoads` | Landing page loads and key UI elements are visible |
| `testSignInOpensLoginOptions` | Sign In button opens the login modal |
| `testLoginOptionsAreVisible` | Google / Apple / Guest options are displayed |
| `testGoogleLoginButtonOpensPopup` | Google OAuth popup opens when clicking Google login |

### Purchase Flow — Web
| Test | Description |
|---|---|
| `testStorePageLoads` | Store page loads via nav button click from home |
| `testSelectBundleShowsBuyButton` | Selecting a bundle reveals the Buy button |
| `testPurchaseFlowStopsBeforePayment` | Full flow up to checkout — stops before payment |

### WebGL Game — Web (Bonus)
| Test | Description |
|---|---|
| `testWebGLSupportedInBrowser` | Browser can run WebGL |
| `testGameCanvasLoadsAfterPlayNow` | Game canvas appears after clicking Play Now |
| `testGameLoadingScreenAppearsAndCompletes` | Loading screen shows and game finishes loading |
| `testGameCanvasIsInteractable` | User can click inside the game canvas |

### Mobile — Android Chrome (Samsung Galaxy A55, Android 16)
| Test | Description |
|---|---|
| `testMobileLandingPageLoads` | Landing page loads on Android Chrome |
| `testMobileSignInOpensLoginOptions` | Sign In opens login modal on mobile |
| `testMobileLoginOptionsDisplayed` | Facebook / email login options visible on mobile |
| `testMobileStorePageLoads` | Store loads on Android Chrome via nav button |
| `testMobilePurchaseFlowStopsBeforePayment` | Purchase flow on mobile — stops before payment |

---

## Key Implementation Notes

### Navigation — UI clicks, not direct URLs
All navigation uses actual UI button clicks matching the user journey, not `driver.get(url)`:
- Store: home → click **Shop** nav link
- Game: home → click **Play Now** button

### Mobile responsiveness — JS-based element resolution
The Stumble Guys site uses responsive Tailwind CSS with duplicate elements (one visible on desktop, one on mobile). Selenium's `visibilityOfElementLocated` only checks the first DOM match — which may be the hidden desktop version on mobile.

**Solution**: `LandingPage` uses `getBoundingClientRect()` in JavaScript to find the element with non-zero dimensions, regardless of DOM order:

```java
// Used for avatar button and login button — works on both desktop and mobile
private void clickVisibleAvatarButton() { ... }   // finds visible avatar
private void clickVisibleLoginButton()  { ... }   // finds visible Login dropdown item
```

### Multi-browser zombie prevention
`WebGLGameTest` and `LoginTest` call `DriverManager.quit()` before re-creating a Chrome instance in the `@BeforeMethod` fallback to prevent zombie processes if the previous browser crashed.

### Mobile ChromeDriver
Mobile tests require a specific ChromeDriver matching the Chrome version on the device. Path is configured in `MobileDriverManager`:
```
~/.appium/chromedriver_150/chromedriver-win32/chromedriver.exe
```
Update this path to match the chromedriver you have installed.

---

## Important Notes

- **No purchase is completed.** The purchase flow navigates to checkout and asserts state without clicking Confirm/Pay.
- **Social login** (Google/Apple/Facebook) requires real OAuth credentials. Tests verify the modal/popup opens.
- **WebGL tests** require Chrome or Firefox. Safari is not tested.
- **Mobile tests** require a physical Android device or AVD with Chrome installed.
- Tests run against the **live production site** — no mocking or network stubbing.
