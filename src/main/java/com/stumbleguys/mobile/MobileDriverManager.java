package com.stumbleguys.mobile;

import com.stumbleguys.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.Duration;
import java.util.stream.Collectors;

import static com.stumbleguys.config.ConfigurationManager.configuration;

public class MobileDriverManager {

    private static final ThreadLocal<WebDriver> MOBILE_DRIVER = new ThreadLocal<>();

    private MobileDriverManager() {
    }

    public static WebDriver getDriver() {
        return MOBILE_DRIVER.get();
    }

    public static void createAndroidChromeDriver() {
        String udid, deviceName, platformVersion;

        String[] detected = detectConnectedDevice();
        if (detected != null) {
            udid          = detected[0];
            deviceName    = detected[1];
            platformVersion = detected[2];
        } else {
            udid          = configuration().androidUdid();
            deviceName    = configuration().androidDeviceName();
            platformVersion = configuration().androidPlatformVersion();
        }

        var options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setDeviceName(deviceName)
                .setUdid(udid)
                .setPlatformVersion(platformVersion)
                .setNewCommandTimeout(Duration.ofSeconds(120));
        options.setCapability("browserName", "Chrome");

        String chromedriverPath = System.getProperty("user.home")
                + "/.appium/chromedriver_150/chromedriver-win32/chromedriver.exe";
        if (new File(chromedriverPath).exists()) {
            options.setCapability("appium:chromedriverExecutable", chromedriverPath);
        }

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

    private static String[] detectConnectedDevice() {
        try {
            Process p = new ProcessBuilder("adb", "devices").start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("List of")) continue;
                    if (line.contains("\tdevice")) {
                        String udid    = line.split("\t")[0].trim();
                        String model   = adbGetProp(udid, "ro.product.model");
                        String version = adbGetProp(udid, "ro.build.version.release");
                        return new String[]{udid, model.isEmpty() ? udid : model, version};
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String adbGetProp(String udid, String prop) {
        try {
            Process p = new ProcessBuilder("adb", "-s", udid, "shell", "getprop", prop).start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                return br.lines().collect(Collectors.joining()).trim();
            }
        } catch (Exception e) {
            return "";
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
