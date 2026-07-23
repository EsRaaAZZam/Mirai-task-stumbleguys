package com.stumbleguys.listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Paths;

public class SuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
    }

    @Override
    public void onFinish(ISuite suite) {
        try {
            String resultsDir = Paths.get("target", "allure-results").toAbsolutePath().toString();
            String reportDir  = Paths.get("target", "allure-report").toAbsolutePath().toString();

            ProcessBuilder pb = new ProcessBuilder(
                    "allure", "generate", resultsDir,
                    "--single-file", "--clean", "-o", reportDir);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                File report = new File(reportDir, "index.html");
                if (report.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(report);
                }
            } else {
                System.err.println("[SuiteListener] allure generate exited with code " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("[SuiteListener] Could not generate Allure report: " + e.getMessage());
        }
    }
}
