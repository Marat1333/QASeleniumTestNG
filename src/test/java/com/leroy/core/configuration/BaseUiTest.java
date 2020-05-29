package com.leroy.core.configuration;

import com.leroy.core.ContextProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.internal.TestResult;

public abstract class BaseUiTest extends BaseTest {

    @BeforeMethod
    @BeforeClass
    @Parameters({"browser", "platform", "host", "environment", "propsFile",
            "build", "timeout"})
    protected void driverCreation(
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

    @AfterMethod
    public void cleanUpAfterMethod(ITestResult tResult) {
        if (tResult.getTestContext().getSuite().getName().equals("Default Suite") ||
                tResult.getStatus() != TestResult.SUCCESS)
            ContextProvider.quitDriver();
        String parallelMethod = tResult.getTestClass().getXmlTest().getSuite().getParallel().name();
        if (parallelMethod.toUpperCase().equals("METHODS")) {
            ContextProvider.quitDriver();
        }
    }

    @AfterClass
    protected void cleanUpAfterClass() {
        ContextProvider.quitDriver();
    }

    protected WebDriver getDriver() {
        return ContextProvider.getDriver();
    }

    // Private methods

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

        ContextProvider.setDriver(
                DriverFactory.createDriver(propsFile, platform, browser, host, timeout, build));
    }

    private boolean isConnectionOpen() {
        RemoteWebDriver driver = (RemoteWebDriver) ContextProvider.getDriver();
        return driver != null && driver.getSessionId() != null;
    }

}

