package com.stumbleguys.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void clear() {
        DRIVER.remove();
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }

    public static String getSessionInfo() {
        var caps = ((RemoteWebDriver) DRIVER.get()).getCapabilities();
        return String.format("browser=%s v=%s platform=%s",
                caps.getBrowserName(), caps.getBrowserVersion(), caps.getPlatformName());
    }
}
