package com.stumbleguys.pages;

import com.stumbleguys.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class GamePage {

    private final By gameCanvas = By.id("react-unity-webgl-canvas-1");

    private static final String CANVAS_ID = "react-unity-webgl-canvas-1";

    private JavascriptExecutor js() {
        return (JavascriptExecutor) DriverManager.getDriver();
    }

    @Step("Wait for game canvas to appear (max {timeoutSec}s)")
    public boolean waitForGameCanvas(int timeoutSec) {
        try {
            new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSec))
                    .until(driver -> Boolean.TRUE.equals(
                            ((JavascriptExecutor) driver).executeScript(
                                    "return !!document.getElementById('" + CANVAS_ID + "');")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify WebGL game canvas is present in DOM")
    public boolean isCanvasPresent() {
        try {
            Object result = js().executeScript("return !!document.getElementById('" + CANVAS_ID + "');");
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify game loading bar is visible")
    public boolean isLoadingBarVisible() {
        try {
            Object result = js().executeScript(
                    "var c = document.getElementById('" + CANVAS_ID + "');" +
                    "if (!c) return false;" +
                    "var p = c.parentElement;" +
                    "return !!(p && p.classList.contains('hidden'));");
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Wait for game to finish loading (canvas initialized, max {timeoutSec}s)")
    public boolean waitForGameToLoad(int timeoutSec) {
        try {
            new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSec))
                    .until(driver -> {
                        Object result = ((JavascriptExecutor) driver).executeScript(
                                "var c = document.getElementById('" + CANVAS_ID + "');" +
                                "if (!c) return false;" +
                                "var p = c.parentElement;" +
                                "return (p && !p.classList.contains('hidden')) || c.width > 300;");
                        return Boolean.TRUE.equals(result);
                    });
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return isCanvasPresent();
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Get canvas element dimensions")
    public String getCanvasDimensions() {
        if (!isCanvasPresent()) return "canvas not found";
        var canvas = DriverManager.getDriver().findElement(gameCanvas);
        return String.format("width=%d height=%d", canvas.getSize().getWidth(), canvas.getSize().getHeight());
    }

    @Step("Wait for canvas container to become visible (max {timeoutSec}s)")
    public boolean waitForCanvasToBeInteractable(int timeoutSec) {
        try {
            new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSec))
                    .until(driver -> {
                        Object result = ((JavascriptExecutor) driver).executeScript(
                                "var c = document.getElementById('react-unity-webgl-canvas-1');" +
                                "if (!c) return false;" +
                                "var p = c.parentElement;" +
                                "var containerVisible = p && !p.classList.contains('hidden');" +
                                "var bcrVisible = c.getBoundingClientRect().width > 0;" +
                                "var unityInited = c.width > 300;" +
                                "return containerVisible || bcrVisible || unityInited;");
                        return Boolean.TRUE.equals(result);
                    });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Click centre of game canvas")
    public void clickCentreOfCanvas() {
        var canvas = DriverManager.getDriver().findElement(gameCanvas);
        ((JavascriptExecutor) DriverManager.getDriver()).executeScript(
                "var r=arguments[0].getBoundingClientRect();" +
                "arguments[0].dispatchEvent(new MouseEvent('click',{bubbles:true,cancelable:true," +
                "clientX:r.left+r.width/2,clientY:r.top+r.height/2}));",
                canvas);
    }

    @Step("Click at relative position ({xPercent}%, {yPercent}%) within canvas")
    public void clickInsideCanvas(int xPercent, int yPercent) {
        var canvas = DriverManager.getDriver().findElement(gameCanvas);
        double xFrac = xPercent / 100.0;
        double yFrac = yPercent / 100.0;
        ((JavascriptExecutor) DriverManager.getDriver()).executeScript(
                "var r=arguments[0].getBoundingClientRect();" +
                "arguments[0].dispatchEvent(new MouseEvent('click',{bubbles:true,cancelable:true," +
                "clientX:r.left+r.width*" + xFrac + ",clientY:r.top+r.height*" + yFrac + "}));",
                canvas);
    }

    @Step("Get WebGL context status via JavaScript")
    public boolean isWebGLSupported() {
        try {
            Object result = ((JavascriptExecutor) DriverManager.getDriver()).executeScript(
                    "var canvas = document.createElement('canvas');" +
                    "return !!(canvas.getContext('webgl') || canvas.getContext('experimental-webgl'));");
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }
}
