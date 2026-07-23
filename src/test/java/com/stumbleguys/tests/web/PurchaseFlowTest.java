package com.stumbleguys.tests.web;

import com.stumbleguys.driver.DriverManager;
import com.stumbleguys.listeners.RetryAnalyzer;
import com.stumbleguys.tests.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static com.stumbleguys.config.ConfigurationManager.configuration;

@Feature("Purchase Flow")
public class PurchaseFlowTest extends BaseTest {

    @BeforeClass(alwaysRun = true)
    @Parameters("browser")
    @Override
    public void
    setUp(@Optional("chrome") String browser) {
        super.setUp(browser);
        landingPage.acceptCookiesIfPresent();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify the Shop page loads and card items are displayed")
    @Story("Shop navigation")
    public void testStorePageLoads() {
        DriverManager.getDriver().get(configuration().url());
        landingPage.clickStore();
        softAssert.assertTrue(storePage.isStoreLoaded(),
                "Shop page should be loaded and show available card items.");
        int count = storePage.getBundleItemCount();
        softAssert.assertTrue(count > 0, "Shop should display at least one purchasable card item.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testStorePageLoads")
    @Description("Verify shop card items have a visible buy/price button")
    @Story("Bundle selection")
    public void testSelectBundleShowsBuyButton() {
        DriverManager.getDriver().get(configuration().url());
        landingPage.clickStore();
        softAssert.assertTrue(storePage.isStoreLoaded(), "Shop must be loaded.");
        storePage.selectFirstBundle();
        softAssert.assertTrue(storePage.isBuyButtonVisible(),
                "Price/buy button should be visible on the first shop card.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testSelectBundleShowsBuyButton")
    @Description("Verify the purchase flow stops before payment — clicking buy shows login gate. " +
                 "Flow: home → click Store nav → click buy button → login modal appears → STOP.")
    @Story("Purchase flow without completing payment")
    public void testPurchaseFlowStopsBeforePayment() {
        DriverManager.getDriver().get(configuration().url());
        landingPage.clickStore();
        softAssert.assertTrue(storePage.isStoreLoaded(), "Shop page must be loaded.");

        softAssert.assertTrue(storePage.isBuyButtonVisible(), "Buy button must be visible.");
        storePage.clickBuyButton();

        softAssert.assertTrue(checkoutPage.isLoginPromptVisible(),
                "Clicking Buy without being logged in must show the login gate — no payment screen should be reachable.");

        softAssert.assertFalse(checkoutPage.isConfirmPaymentButtonVisible(),
                "SAFETY: Confirm Payment button must NOT be reachable — purchase flow stopped before payment.");

        System.out.println("[PURCHASE FLOW] Login gate shown. URL: " + checkoutPage.getCurrentUrl());
        System.out.println("[PURCHASE FLOW] Test complete — no payment was made.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify Shop nav link on landing page navigates to the shop")
    @Story("Store navigation from home")
    public void testLandingPageStoreNavigation() {
        DriverManager.getDriver().get(configuration().url());
        softAssert.assertTrue(landingPage.isStoreLinkVisible(),
                "Shop link should be visible in the landing page navigation.");
        if (landingPage.isStoreLinkVisible()) {
            landingPage.clickStore();
            softAssert.assertTrue(storePage.isStoreLoaded(),
                    "Clicking the Shop link should navigate to the shop page.");
        }
        softAssert.assertAll();
    }
}
