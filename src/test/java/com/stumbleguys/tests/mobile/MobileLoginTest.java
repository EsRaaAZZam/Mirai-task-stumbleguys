package com.stumbleguys.tests.mobile;

import com.stumbleguys.listeners.RetryAnalyzer;
import io.qameta.allure.Description;

import static com.stumbleguys.config.ConfigurationManager.configuration;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Feature("Authentication - Mobile")
public class MobileLoginTest extends MobileBaseTest {

    @BeforeClass(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        landingPage.acceptCookiesIfPresent();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify the Stumble Guys landing page loads correctly on Android Chrome")
    @Story("Mobile landing page")
    public void testMobileLandingPageLoads() {
        softAssert.assertTrue(landingPage.isLandingPageLoaded(),
                "Landing page should load correctly on Android Chrome.");
        softAssert.assertTrue(landingPage.isSignInButtonVisible(),
                "Login button should be visible on mobile.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testMobileLandingPageLoads")
    @Description("Verify clicking Login on Android Chrome opens the login modal")
    @Story("Mobile login modal")
    public void testMobileSignInOpensLoginOptions() {
        landingPage.clickSignIn();
        softAssert.assertTrue(loginPage.isLoginVisible(),
                "Login modal should be visible on Android Chrome after tapping Login.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testMobileSignInOpensLoginOptions")
    @Description("Verify Facebook and email login options are visible on mobile")
    @Story("Mobile login options")
    public void testMobileLoginOptionsDisplayed() {
        landingPage.clickSignIn();
        softAssert.assertTrue(
                loginPage.isFacebookLoginButtonVisible() || loginPage.isEmailLoginButtonVisible(),
                "Facebook or email login option must be visible on the mobile login modal.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testMobileLoginOptionsDisplayed")
    @Description("Verify clicking Continue with Facebook navigates to Facebook OAuth on mobile")
    @Story("Mobile Facebook login")
    public void testMobileFacebookLoginNavigatesOAuth() {
        navigateTo(configuration().url());
        landingPage.clickSignIn();
        loginPage.clickFacebookLogin();
        softAssert.assertTrue(loginPage.isFacebookUrlReached(),
                "Browser should navigate to Facebook OAuth page after clicking Continue with Facebook on mobile.");
        navigateTo(configuration().url());
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testMobileLoginOptionsDisplayed")
    @Description("Verify clicking Continue with email opens the email login form on mobile")
    @Story("Mobile email login")
    public void testMobileEmailLoginOpensEmailForm() {
        navigateTo(configuration().url());
        landingPage.clickSignIn();
        loginPage.clickEmailLogin();
        softAssert.assertTrue(loginPage.isEmailInputVisible(),
                "Email input field should appear after clicking Continue with email on mobile.");
        softAssert.assertAll();
    }
}
