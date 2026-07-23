package com.stumbleguys.pages;

import com.stumbleguys.driver.DriverManager;
import com.stumbleguys.elementActions.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final Element element = new Element();

    private final By loginModal       = By.cssSelector("[data-headlessui-state='open'][role='dialog']");
    private final By facebookLoginBtn = By.xpath("//button[contains(normalize-space(),'Continue with Facebook')]");
    private final By emailLoginBtn    = By.xpath("//button[contains(normalize-space(),'Continue with email')]");
    private final By emailInput       = By.cssSelector("input[name='email']");

    @Step("Verify login modal is displayed")
    public boolean isLoginVisible() {
        return element.isVisible(loginModal, 10);
    }

    @Step("Verify Facebook login button is visible")
    public boolean isFacebookLoginButtonVisible() {
        return element.isVisible(facebookLoginBtn, 10);
    }

    @Step("Verify email login button is visible")
    public boolean isEmailLoginButtonVisible() {
        return element.isVisible(emailLoginBtn, 10);
    }

    @Step("Click Continue with Facebook")
    public void clickFacebookLogin() {
        element.click(facebookLoginBtn);
    }

    @Step("Click Continue with email")
    public void clickEmailLogin() {
        element.click(emailLoginBtn);
    }

    @Step("Verify Facebook OAuth opened (popup or redirect)")
    public boolean isFacebookPopupOpen(String mainWindowHandle) {
        try {
            new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10))
                .until(d -> d.getWindowHandles().size() > 1 ||
                            (d.getCurrentUrl() != null && d.getCurrentUrl().contains("facebook.com")));
            if (DriverManager.getDriver().getWindowHandles().size() > 1) {
                for (String handle : DriverManager.getDriver().getWindowHandles()) {
                    if (!handle.equals(mainWindowHandle)) {
                        String url = DriverManager.getDriver().switchTo().window(handle).getCurrentUrl();
                        boolean isFacebook = url != null && url.contains("facebook.com");
                        DriverManager.getDriver().close();
                        DriverManager.getDriver().switchTo().window(mainWindowHandle);
                        return isFacebook;
                    }
                }
            }
            return DriverManager.getDriver().getCurrentUrl() != null &&
                   DriverManager.getDriver().getCurrentUrl().contains("facebook.com");
        } catch (Exception ignored) {}
        return false;
    }

    @Step("Verify page navigated to Facebook OAuth")
    public boolean isFacebookUrlReached() {
        try {
            new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(15))
                .until(d -> d.getCurrentUrl() != null && d.getCurrentUrl().contains("facebook.com"));
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    @Step("Verify email input field is visible")
    public boolean isEmailInputVisible() {
        return element.isVisible(emailInput, 10);
    }
}
