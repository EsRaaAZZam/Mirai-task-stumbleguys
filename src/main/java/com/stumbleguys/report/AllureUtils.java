package com.stumbleguys.report;

import com.stumbleguys.driver.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class AllureUtils {

    private AllureUtils() {
    }

    public static void attachScreenshot(String name) {
        try {
            if (!DriverManager.hasDriver()) return;
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    public static void attachScreenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            attachScreenshot("Failure - " + result.getMethod().getMethodName());
        }
    }

}
