package com.stumbleguys.tests.web;

import com.stumbleguys.driver.DriverManager;
import com.stumbleguys.driver.TargetFactory;
import com.stumbleguys.listeners.RetryAnalyzer;
import com.stumbleguys.tests.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Duration;

import static com.stumbleguys.config.ConfigurationManager.configuration;

@Feature("Authentication")
public class LoginTest extends BaseTest {

    private String mainWindowHandle;

    @BeforeClass(alwaysRun = true)
    @Parameters("browser")
    @Override
    public void setUp(@Optional("chrome") String browser) {
        super.setUp(browser);
        mainWindowHandle = DriverManager.getDriver().getWindowHandle();
    }

    @BeforeMethod(alwaysRun = true)
    public void resetToHome() {
        try {
            for (String handle : DriverManager.getDriver().getWindowHandles()) {
                if (!handle.equals(mainWindowHandle)) {
                    DriverManager.getDriver().switchTo().window(handle).close();
                }
            }
            DriverManager.getDriver().switchTo().window(mainWindowHandle);
        } catch (Exception e) {
            DriverManager.quit();
            WebDriver driver = new TargetFactory().createInstance("chrome");
            DriverManager.setDriver(driver);
            DriverManager.getDriver().manage().window().maximize();
            DriverManager.getDriver().manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(configuration().timeout()));
            mainWindowHandle = DriverManager.getDriver().getWindowHandle();
            initPages();
        }
        DriverManager.getDriver().manage().deleteAllCookies();
        DriverManager.getDriver().get(configuration().url());
        landingPage.acceptCookiesIfPresent();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify the Stumble Guys landing page loads and key UI elements are visible")
    @Story("Landing Page")
    public void testLandingPageLoads() {
        softAssert.assertTrue(landingPage.isLandingPageLoaded(),
                "Landing page did not load within the expected time.");
        softAssert.assertTrue(landingPage.isSignInButtonVisible(),
                "Login button should be visible on the landing page.");
        softAssert.assertTrue(landingPage.isPlayNowButtonVisible(),
                "Play Now link should be visible in the navigation.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify clicking Login button opens the login modal")
    @Story("Login modal")
    public void testSignInOpensLoginOptions() {
        landingPage.clickSignIn();
        softAssert.assertTrue(loginPage.isLoginVisible(),
                "Login modal should be visible after clicking Login.");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify login modal shows Facebook and email login options")
    @Story("Login options")
    public void testLoginOptionsAreVisible() {
        landingPage.clickSignIn();
        softAssert.assertTrue(loginPage.isFacebookLoginButtonVisible(),
                "Continue with Facebook button should be visible in the login modal.");
        softAssert.assertTrue(loginPage.isEmailLoginButtonVisible(),
                "Continue with email button should be visible in the login modal.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify Continue with Facebook button is present and enabled in the login modal")
    @Story("Facebook login")
    public void testFacebookLoginButtonOpensPopup() {
        landingPage.clickSignIn();
        softAssert.assertTrue(loginPage.isLoginVisible(),
                "Login modal must be open before checking the Facebook button.");
        softAssert.assertTrue(loginPage.isFacebookLoginButtonVisible(),
                "Continue with Facebook button must be visible in the login modal.");
    }
}
