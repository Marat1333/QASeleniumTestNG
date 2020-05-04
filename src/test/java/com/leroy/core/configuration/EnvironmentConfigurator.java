package com.leroy.core.configuration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class EnvironmentConfigurator {
    protected RemoteWebDriver driver = null;

    private boolean evalAfterClass = true;
    private boolean evalAfterMethod = true;
    private boolean evalBeforeMethod = true;
    private boolean evalBeforeClass = true;

    @BeforeMethod
    @BeforeClass
    @Parameters({"browser", "platform", "host", "environment", "propsFile",
            "build", "timeout"})
    protected void configuration(
            @Optional("") String browser,
            @Optional("") String platform,
            @Optional("") String host,
            @Optional("") String environment,
            @Optional("") String propsFile,
            @Optional("") String build,
            @Optional("") String timeout) throws Exception {
        if (!isConnectionOpen()) {
            createDriver(browser, platform, host, environment, propsFile, build, timeout);
        }
    }

    private void createDriver(String browser, String platform, String host, String environment,
                              String propsFile, String build, String timeout) throws Exception {
        browser = System.getProperty("mbrowser", browser);
        platform = System.getProperty("mplatform", platform);
        host = System.getProperty(DriverFactory.HOST_ENV_VAR, host);
        //environment = System.getProperty("menv", environment);
        propsFile = System.getProperty("mpropsFile", propsFile);
        build = System.getProperty("mbuild", build);
        timeout = System.getProperty("mtimeout", timeout);

        if (propsFile.isEmpty()) {
            throw new Exception("Property file should be specified");
        }

        driver = DriverFactory.createDriver(propsFile, platform, browser, host, timeout, build);
    }

    public WebDriver getDriver() {
        return driver;
    }

    private boolean isConnectionOpen() {
        return driver != null && driver.getSessionId() != null;
    }

    protected boolean isEvalAfterClass() {
        return evalAfterClass;
    }

    protected void setEvalAfterClass(boolean evalAfterClass) {
        this.evalAfterClass = evalAfterClass;
    }

    protected boolean isEvalAfterMethod() {
        return evalAfterMethod;
    }

    protected void setEvalAfterMethod(boolean evalAfterMethod) {
        this.evalAfterMethod = evalAfterMethod;
    }

    protected boolean isEvalBeforeMethod() {
        return evalBeforeMethod;
    }

    protected void setEvalBeforeMethod(boolean evalBeforeMethod) {
        this.evalBeforeMethod = evalBeforeMethod;
    }

    protected boolean isEvalBeforeClass() {
        return evalBeforeClass;
    }

    protected void setEvalBeforeClass(boolean evalBeforeClass) {
        this.evalBeforeClass = evalBeforeClass;
    }

    protected void disableAllCheck() {
        evalAfterMethod = false;
        evalBeforeMethod = false;
        evalAfterClass = false;
        evalBeforeClass = false;
    }

    protected void enableAllCheck() {
        evalAfterMethod = true;
        evalBeforeMethod = true;
        evalAfterClass = true;
        evalBeforeClass = true;
    }
}

