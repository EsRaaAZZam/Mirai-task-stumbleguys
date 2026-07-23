package com.stumbleguys.pages;

import com.stumbleguys.driver.DriverManager;
import com.stumbleguys.elementActions.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LandingPage {

    private final Element element = new Element();

    private final By cookieAcceptBtn = By.id("onetrust-accept-btn-handler");
    private final By playNavLink     = By.cssSelector("a[href='/play']");
    private final By shopNavLink     = By.cssSelector("a[href='/shop']");
    private final By navBar          = By.cssSelector("nav.fixed");

    @Step("Accept cookie consent if present")
    public void acceptCookiesIfPresent() {
        if (element.isVisible(cookieAcceptBtn,3 )) {
            element.click(cookieAcceptBtn);
            return;
        }
        try {
            ((JavascriptExecutor) DriverManager.getDriver()).executeScript(
                "var aside = document.getElementById('usercentrics-cmp-ui');" +
                "if (aside && aside.shadowRoot) {" +
                "  var btns = Array.from(aside.shadowRoot.querySelectorAll('button'));" +
                "  var accept = btns.find(function(b){ return b.textContent.trim() === 'Accept All'; });" +
                "  if (accept) accept.click();" +
                "}"
            );
            new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(5))
                .until(d -> Boolean.TRUE.equals(((JavascriptExecutor) d).executeScript(
                    "var aside = document.getElementById('usercentrics-cmp-ui');" +
                    "if (!aside || !aside.shadowRoot) return true;" +
                    "var modal = aside.shadowRoot.querySelector('.cmp');" +
                    "return !modal || modal.getBoundingClientRect().height === 0;"
                )));
        } catch (Exception ignored) {}
    }

    @Step("Verify landing page is loaded")
    public boolean isLandingPageLoaded() {
        element.isPageLoaded();
        return element.isVisible(navBar, 15);
    }

    @Step("Click Play Now nav link")
    public void clickPlayNow() {
        if (element.isVisible(playNavLink, 3)) {
            element.click(playNavLink);
        } else {
            ((JavascriptExecutor) DriverManager.getDriver()).executeScript(
                    "document.querySelector(\"a[href='/play']\").click();");
        }
    }

    private void clickVisibleAvatarButton() {
        new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10))
            .until(d -> Boolean.TRUE.equals(((JavascriptExecutor) d).executeScript(
                "var btns = document.querySelectorAll('button');" +
                "for (var i = 0; i < btns.length; i++) {" +
                "  if (!btns[i].querySelector('img[alt=\"avatar\"]')) continue;" +
                "  var r = btns[i].getBoundingClientRect();" +
                "  if (r.width > 0 && r.height > 0) { btns[i].click(); return true; }" +
                "}" +
                "return false;"
            )));
    }

    @Step("Click Login button")
    public void clickSignIn() {
        clickVisibleAvatarButton();
        clickVisibleLoginButton();
    }

    private void clickVisibleLoginButton() {
        new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10))
            .until(d -> Boolean.TRUE.equals(((JavascriptExecutor) d).executeScript(
                "var btns = document.querySelectorAll('button.AuthButton_login__fPi4G');" +
                "for (var i = 0; i < btns.length; i++) {" +
                "  var r = btns[i].getBoundingClientRect();" +
                "  if (r.width > 0 && r.height > 0) { btns[i].click(); return true; }" +
                "}" +
                "return false;"
            )));
    }

    @Step("Click Shop nav link")
    public void clickStore() {
        if (element.isVisible(shopNavLink, 3)) {
            element.click(shopNavLink);
        } else {
            ((JavascriptExecutor) DriverManager.getDriver()).executeScript(
                    "document.querySelector(\"a[href='/shop']\").click();");
        }
    }

    @Step("Verify nav avatar/login button is visible")
    public boolean isSignInButtonVisible() {
        try {
            Object result = ((JavascriptExecutor) DriverManager.getDriver()).executeScript(
                "var btns = document.querySelectorAll('button');" +
                "for (var i = 0; i < btns.length; i++) {" +
                "  if (!btns[i].querySelector('img[alt=\"avatar\"]')) continue;" +
                "  var r = btns[i].getBoundingClientRect();" +
                "  if (r.width > 0 && r.height > 0) return true;" +
                "}" +
                "return false;");
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify Play Now link is visible")
    public boolean isPlayNowButtonVisible() {
        return element.isVisible(playNavLink, 10);
    }

    @Step("Verify Shop link is visible")
    public boolean isStoreLinkVisible() {
        return element.isVisible(shopNavLink, 10);
    }
}
