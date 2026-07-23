package com.stumbleguys.pages;

import com.stumbleguys.elementActions.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class LoginPage {

    private final Element element = new Element();

    private final By loginModal       = By.cssSelector("[data-headlessui-state='open'][role='dialog']");
    private final By facebookLoginBtn = By.xpath("//button[contains(normalize-space(),'Continue with Facebook')]");
    private final By emailLoginBtn    = By.xpath("//button[contains(normalize-space(),'Continue with email')]");
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
}
