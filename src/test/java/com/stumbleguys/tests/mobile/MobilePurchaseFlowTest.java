package com.stumbleguys.tests.mobile;

import com.stumbleguys.listeners.RetryAnalyzer;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.stumbleguys.config.ConfigurationManager.configuration;

@Feature("Purchase Flow - Mobile")
public class MobilePurchaseFlowTest extends MobileBaseTest {

    @BeforeClass(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        landingPage.acceptCookiesIfPresent();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify the Store page loads correctly on Android Chrome")
    @Story("Mobile store navigation")
    public void testMobileStorePageLoads() {
        navigateTo(configuration().url());
        landingPage.clickStore();
        softAssert.assertTrue(storePage.isStoreLoaded(),
                "Store page should load on Android Chrome.");
        softAssert.assertTrue(storePage.getBundleItemCount() > 0,
                "Store should display at least one bundle on mobile.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testMobileStorePageLoads")
    @Description("Verify the mobile purchase flow stops before payment — no actual payment is made. " +
                 "Flow: home → click Store nav → select bundle → click buy → login gate appears → STOP.")
    @Story("Mobile purchase flow without completing payment")
    public void testMobilePurchaseFlowStopsBeforePayment() {
        navigateTo(configuration().url());
        landingPage.clickStore();
        softAssert.assertTrue(storePage.isStoreLoaded(), "Store must load on mobile.");

        storePage.selectFirstBundle();
        softAssert.assertTrue(storePage.isBuyButtonVisible(),
                "Buy button must appear after selecting a bundle on mobile.");
        storePage.clickBuyButton();

        softAssert.assertTrue(checkoutPage.isLoginPromptVisible(),
                "Tapping Buy without being logged in must show the login gate on mobile.");

        softAssert.assertFalse(checkoutPage.isConfirmPaymentButtonVisible(),
                "SAFETY: Confirm Payment button must NOT be reachable — purchase stops before payment.");

        System.out.println("[MOBILE PURCHASE FLOW] Login gate shown. URL: " + checkoutPage.getCurrentUrl());
        System.out.println("[MOBILE PURCHASE FLOW] Test complete — no payment was made.");
        softAssert.assertAll();
    }
}
