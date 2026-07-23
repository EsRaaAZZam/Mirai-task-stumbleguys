package com.stumbleguys.utils;

import com.stumbleguys.driver.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.asserts.SoftAssert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CustomSoftAssert {

    private final SoftAssert softAssert = new SoftAssert();

    public void assertTrue(boolean condition, String message) {
        if (condition) {
            logStepWithScreenshot("Verified: " + message, Status.PASSED);
        } else {
            logStepWithScreenshot("Assertion failed: " + message, Status.FAILED);
            softAssert.fail(message);
        }
    }

    public void assertTrue(boolean condition) {
        assertTrue(condition, "Expected condition to be true");
    }

    public void assertFalse(boolean condition, String message) {
        if (!condition) {
            logStepWithScreenshot("Verified: " + message, Status.PASSED);
        } else {
            logStepWithScreenshot("Assertion failed: " + message, Status.FAILED);
            softAssert.fail(message);
        }
    }

    public void assertFalse(boolean condition) {
        assertFalse(condition, "Expected condition to be false");
    }

    public void assertEquals(Object actual, Object expected, String message) {
        boolean passed = actual != null && actual.equals(expected);
        if (passed) {
            logStepWithScreenshot("Verified: " + message, Status.PASSED);
        } else {
            String detail = message + " — Expected: [" + expected + "], Actual: [" + actual + "]";
            logStepWithScreenshot("Assertion failed: " + detail, Status.FAILED);
            softAssert.fail(detail);
        }
    }

    public void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, "Values do not match");
    }

    public void assertNotNull(Object object, String message) {
        if (object != null) {
            logStepWithScreenshot("Verified: " + message, Status.PASSED);
        } else {
            logStepWithScreenshot("Assertion failed: " + message, Status.FAILED);
            softAssert.fail(message);
        }
    }

    public void fail(String message) {
        logStepWithScreenshot("Assertion failed: " + message, Status.FAILED);
        softAssert.fail(message);
    }

    public void assertAll() {
        softAssert.assertAll();
    }

    private void logStepWithScreenshot(String message, Status status) {
        String stepUuid = String.valueOf(System.currentTimeMillis());
        StepResult stepResult = new StepResult().setName(message).setStatus(status);
        Allure.getLifecycle().startStep(stepUuid, stepResult);
        attachScreenshot();
        Allure.getLifecycle().stopStep(stepUuid);
    }

    private void attachScreenshot() {
        try {
            byte[] raw = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(raw));
            int w = original.getWidth() / 2;
            int h = original.getHeight() / 2;
            BufferedImage resized = new BufferedImage(w, h, original.getType());
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(original, 0, 0, w, h, null);
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "png", baos);
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(baos.toByteArray()));
        } catch (Exception ignored) {
        }
    }
}
