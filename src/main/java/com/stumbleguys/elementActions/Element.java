package com.stumbleguys.elementActions;

import com.stumbleguys.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Element {

    private static final int DEFAULT_TIMEOUT_SEC = 30;

    public WebDriverWait getWait(long timeoutSeconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    private JavascriptExecutor js() {
        return (JavascriptExecutor) DriverManager.getDriver();
    }

    public WebElement find(By locator) {
        getWait(DEFAULT_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
        return DriverManager.getDriver().findElement(locator);
    }

    public List<WebElement> findAll(By locator) {
        getWait(DEFAULT_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        return DriverManager.getDriver().findElements(locator);
    }

    @Step("Click on element: {locator}")
    public void click(By locator) {
        try {
            waitForClickable(locator);
            find(locator).click();
        } catch (ElementClickInterceptedException | TimeoutException e) {
            clickViaJavaScript(locator);
        }
    }

    @Step("Click via JavaScript")
    public void clickViaJavaScript(By locator) {
        js().executeScript("arguments[0].click();", DriverManager.getDriver().findElement(locator));
    }

    public void clickViaJavaScript(WebElement element) {
        js().executeScript("arguments[0].click();", element);
    }

    public boolean isVisible(By locator) {
        try {
            waitForVisible(locator);
            return find(locator).isDisplayed();
        } catch (TimeoutException | NoSuchElementException ex) {
            return false;
        }
    }

    public boolean isVisible(By locator, int timeoutSec) {
        try {
            getWait(timeoutSec).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException ex) {
            return false;
        }
    }

    public boolean isNotVisible(By locator, int timeoutSec) {
        try {
            return getWait(timeoutSec).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public void waitForVisible(By locator) {
        getWait(DEFAULT_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForClickable(By locator) {
        getWait(DEFAULT_TIMEOUT_SEC).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForInvisible(By locator, int timeoutSec) {
        getWait(timeoutSec).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    @Step("Scroll element into view")
    public void scrollIntoView(By locator) {
        WebElement el = getWait(DEFAULT_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
        js().executeScript("arguments[0].scrollIntoView({block:'center',inline:'nearest'});", el);
    }

    public void scrollIntoView(WebElement element) {
        js().executeScript("arguments[0].scrollIntoView({block:'center',inline:'nearest'});", element);
    }

    public Object executeScript(String script, Object... args) {
        return js().executeScript(script, args);
    }

    public boolean isPageLoaded() {
        try {
            return getWait(30).until(d ->
                    "complete".equals(js().executeScript("return document.readyState")));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
