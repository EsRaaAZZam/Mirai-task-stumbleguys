package com.stumbleguys.mobile;

import com.stumbleguys.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;

import java.net.URI;
import java.time.Duration;

import static com.stumbleguys.config.ConfigurationManager.configuration;

public class MobileDriverManager {

    private static final ThreadLocal<WebDriver> MOBILE_DRIVER = new ThreadLocal<>();

    private MobileDriverManager() {
    }

    public static WebDriver getDriver() {
        return MOBILE_DRIVER.get();
    }

    public static void createAndroidChromeDriver() {
        var options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setDeviceName(configuration().androidDeviceName())
                .setUdid(configuration().androidUdid())
                .setPlatformVersion(configuration().androidPlatformVersion())
                .setNewCommandTimeout(Duration.ofSeconds(120));
        options.setCapability("browserName", "Chrome");
        options.setCapability("appium:chromedriverExecutable",
                System.getProperty("user.home") + "/.appium/chromedriver_150/chromedriver-win32/chromedriver.exe");

        try {
            var driver = new AndroidDriver(
                    URI.create(configuration().appiumServerUrl()).toURL(),
                    options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            MOBILE_DRIVER.set(driver);
            DriverManager.setDriver(driver);
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + configuration().appiumServerUrl(), e);
        }
    }

    public static void quit() {
        WebDriver driver = MOBILE_DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting mobile driver: " + e.getMessage());
            } finally {
                MOBILE_DRIVER.remove();
                DriverManager.clear();
            }
        }
    }
}
