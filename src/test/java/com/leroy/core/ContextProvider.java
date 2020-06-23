package com.leroy.core;

import org.openqa.selenium.WebDriver;

public class ContextProvider {

    private static final ThreadLocal<Context> context = new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> drivers = new ThreadLocal<>();

    public static Context getContext() {
        return context.get();
    }

    public static void setContext(Context testContext) {
        context.set(testContext);
    }

    public static WebDriver getDriver() {
        return drivers.get();
    }

    public static void setDriver(WebDriver driver) {
        drivers.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.quit();
        }
    }

}
