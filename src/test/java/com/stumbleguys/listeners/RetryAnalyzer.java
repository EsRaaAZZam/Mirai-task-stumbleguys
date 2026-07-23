package com.stumbleguys.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import static com.stumbleguys.config.ConfigurationManager.configuration;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetry = configuration().retryCount();
        if (retryCount < maxRetry) {
            retryCount++;
            System.out.printf("Retrying test '%s' — attempt %d of %d%n",
                    result.getMethod().getMethodName(), retryCount, maxRetry);
            return true;
        }
        return false;
    }
}
