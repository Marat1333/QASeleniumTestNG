package com.leroy.core.listeners.helpers;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    int counter = 0;
    int retryLimit = 0;

    int forceCounter = 0;

    private static final ThreadLocal<Boolean> forceRetry = new ThreadLocal<>();

    public static void enableForceRetry() {
        forceRetry.set(true);
    }

    public static void disableForceRetry() {
        forceRetry.set(false);
    }

    /*
     * (non-Javadoc)
     * @see org.testng.IRetryAnalyzer#retry(org.testng.ITestResult)
     *
     * This method decides how many times a test needs to be rerun.
     * TestNg will call this method every time a test fails. So we
     * can put some code in here to decide when to rerun the test.
     *
     * Note: This method will return true if a tests needs to be retried
     * and false it not.
     *
     */

    @Override
    public boolean retry(ITestResult result) {

        if (forceRetry.get() != null && forceRetry.get() && forceCounter < 1) {
            disableForceRetry();
            forceCounter++;
            return true;
        }

        retryLimit = Integer.parseInt(System.getProperty("retryOnFailCount", "0"));

        if (counter < retryLimit) {
            counter++;
            return true;
        }

        return false;
    }
}

