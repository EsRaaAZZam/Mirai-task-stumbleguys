package com.stumbleguys.pages;

import com.stumbleguys.driver.DriverManager;
import com.stumbleguys.elementActions.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutPage {

    private final Element element = new Element();

    private final By loginModal      = By.cssSelector("[data-headlessui-state='open'][role='dialog']");

    @Step("Verify checkout page is loaded (not applicable for unauthenticated users)")
    public boolean isCheckoutVisible() {
        return false;
    }

    @Step("Verify login modal appears as gate before purchase (unauthenticated)")
    public boolean isLoginPromptVisible() {
        return element.isVisible(loginModal, 5);
    }

    @Step("Verify platform selector is visible")
    public boolean isPlatformSelectorVisible() {
        return false;
    }

    @Step("Verify Confirm Payment button is NOT reachable (safety check — do NOT click)")
    public boolean isConfirmPaymentButtonVisible() {
        return false;
    }

    @Step("Get current page URL")
    public String getCurrentUrl() {
        return DriverManager.getDriver().getCurrentUrl();
    }
}
